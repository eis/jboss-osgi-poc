package fi.eis.applications.jboss.poc.osgiservice;

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

	BundleContext context;

	@Override
	public String getMessage() {

		DataSource ds = null;
		
		try {
			ds = getDataSource();
		} catch (IllegalStateException ex) {
			log.error("error getting data source", ex);
			return "Error getting data source: " + ex.getMessage();
		}

		log.info("lookup result is " + ds);

		return "JNDI lookup result: " + ds;

	}


	private DataSource getDataSource() {

		context = FrameworkUtil.getBundle(JDBCService.class).getBundleContext();
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
