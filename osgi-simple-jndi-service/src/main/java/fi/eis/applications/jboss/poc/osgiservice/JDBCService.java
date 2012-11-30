package fi.eis.applications.jboss.poc.osgiservice;

import java.sql.Connection;
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
			return "Got from database: " + getResultFrom(conn, "Hello");
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
		final String GET_FT_SEARCH_SCORE =
			String.format("SELECT * FROM FT_SEARCH('%s', 0, 0)", searchWord);
		final String GET_FT_SEARCH_DATA =
			String.format(
				"SELECT T.* FROM FT_SEARCH_DATA('%s', 0, 0) FT, TEST T"+
				" WHERE FT.TABLE='TEST' AND T.ID=FT.KEYS[0]", searchWord);

		String score = null, query = null;
		
		Statement st = null;
		ResultSet rs = null;

		try {
			st = conn.createStatement();
			rs = st.executeQuery(GET_FT_SEARCH_SCORE);
			if (rs.next()) {
				score = rs.getString("score");
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			close(rs);
			close(st);
		}
		
		try {
			st = conn.createStatement();
			rs = st.executeQuery(GET_FT_SEARCH_DATA);
			if (rs.next()) {
				query = rs.getString("name");
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			close(rs);
			close(st);
		}
		
		return String.format("%s (score: %s)", query, score);
	}

	private void addData(Connection conn) {
		final String CREATE_FULLTEXT_INIT = "CREATE ALIAS IF NOT EXISTS FT_INIT FOR \"org.h2.fulltext.FullText.init\"";
		final String CALL_FULLTEXT_INIT = "CALL FT_INIT()";
		final String DROP_PREV_TABLE = "DROP TABLE TEST IF EXISTS";
		final String CREATE_TABLE = "CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR)";
		final String INSERT_DATA = "INSERT INTO TEST VALUES(1, 'Hello World')";
		final String DROP_PREV_INDEX = "CALL FT_DROP_INDEX('PUBLIC', 'TEST')";
		final String CREATE_INDEX = "CALL FT_CREATE_INDEX('PUBLIC', 'TEST', NULL)";
		final String REINDEX = "CALL FT_REINDEX()";
		
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute(CREATE_FULLTEXT_INIT);
			st.execute(CALL_FULLTEXT_INIT);
			st.execute(DROP_PREV_TABLE);
			st.execute(CREATE_TABLE);
			st.execute(INSERT_DATA);
			st.execute(DROP_PREV_INDEX);
			st.execute(CREATE_INDEX);
			st.execute(REINDEX);
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
