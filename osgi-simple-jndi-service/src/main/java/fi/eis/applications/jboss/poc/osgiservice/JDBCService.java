package fi.eis.applications.jboss.poc.osgiservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.jboss.logging.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIContextManager;

import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

public class JDBCService implements MessageService {

	private static Logger log = Logger.getLogger(JDBCService.class);

	private static final String JBOSS_DEFAULT_DATA_SOURCE_JNDI_NAME = "java:jboss/datasources/ExampleDS";

	private static final String JBOSS_DEFAULT_DATA_SOURCE_PASS = "sa";

	private static final String JBOSS_DEFAULT_DATA_SOURCE_USER = "sa";

	@Override
	public String getMessage() {

		DataSource ds = null;
		Connection conn = null;
		
		try {
			ds = getDataSource();
			conn = getConnection(ds);
			addData(conn);
			return "Got from database: " + getResultFrom(conn, "Hello%");
		} catch (IllegalStateException ex) {
			log.error("error working with data source", ex);
			return "Error working with data source: " + ex.getMessage();
		} finally {
			close(conn);
		}
	}



	/**
	 * For the sake of testing, we do a full-text search here.
	 * 
	 * @param conn connection to the database containig data
	 * @return search data as well as the score for that data
	 * @url http://stackoverflow.com/questions/6641737/how-to-use-full-text-search-in-h2-database
	 */
	private String getResultFrom(Connection conn, String searchWord) {
		final String GET_NAME =
			"SELECT name FROM test WHERE name LIKE ?";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(GET_NAME);
			ps.setString(1, searchWord);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			close(rs);
			close(ps);
		}
		return "";
	}

	private void addData(Connection conn) {
		final String DROP_PREV_TABLE = "DROP TABLE test IF EXISTS";
		final String CREATE_TABLE = "CREATE TABLE test(id INT PRIMARY KEY, name VARCHAR)";
		final String INSERT_DATA = "INSERT INTO test VALUES(1, 'Hello World')";
		
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute(DROP_PREV_TABLE);
			st.execute(CREATE_TABLE);
			st.execute(INSERT_DATA);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			close(st);
		}
		
	}

	private void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new IllegalStateException("Couldn't close", e);
			}
		}
	}

	private void close(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new IllegalStateException("Couldn't close", e);
			}
		}
	}


	private void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new IllegalStateException("Couldn't close", e);
			}
		}
	}


	private static Connection getConnection(DataSource ds) {
		try {
			return ds.getConnection(JBOSS_DEFAULT_DATA_SOURCE_USER, JBOSS_DEFAULT_DATA_SOURCE_PASS);
		} catch (SQLException e) {
			throw new IllegalStateException("Getting a connection failed", e);
		}
	}


	private static DataSource getDataSource() {

		BundleContext context = FrameworkUtil.getBundle(JDBCService.class).getBundleContext();
		log.debug("context is " + context);

		// obtain JNDIContextManager service from the OSGi BundleContext
		ServiceReference serviceReference = context
				.getServiceReference(JNDIContextManager.class.getName());

		log.debug("jndi context manager reference is " + serviceReference);

		JNDIContextManager contextManager = (JNDIContextManager) context
				.getService(serviceReference);

		log.debug("jndi context manager is " + contextManager);

		// create a context with the default environment setup
		Context initialContext = null;
		try {
			initialContext = contextManager.newInitialContext();
		} catch (NamingException e) {
			throw new IllegalStateException("JNDI lookup failed", e);
		}

		log.debug("jndi initial context is " + initialContext);

		try {
			return (DataSource) initialContext
					.lookup(JBOSS_DEFAULT_DATA_SOURCE_JNDI_NAME);
		} catch (NamingException e) {
			throw new IllegalStateException( "DS lookup failed", e);
		}

		/*
	     * Another alternative:
	     * 
	     * JNDIContextManager contextManager = NamingSupport.provideJNDIIntegration(
	     * context, bundle);
	     * create a context with the default environment setup
	     * Context initialContext = contextManager.newInitialContext();
	     */
	}

}
