package fi.eis.applications.jboss.poc.osgiservice;

import org.jboss.logging.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

public class JDBCService implements MessageService {

  private static Logger log = Logger.getLogger(JDBCService.class);

  BundleContext context;

  @Override
  public String getMessage() {

    context = FrameworkUtil.getBundle(JDBCService.class).getBundleContext();
    log.info("context is " + context);
    // obtain JNDIContextManager service from the OSGi BundleContext
    
    ServiceReference serviceReference = context
        .getServiceReference("org.osgi.service.jndi.JNDIContextManager");

    log.info("reference is " + serviceReference);

    // JNDIContextManager contextManager = NamingSupport.provideJNDIIntegration(
    // context, bundle);
    // create a context with the default environment setup
    // Context initialContext = contextManager.newInitialContext();
    return "JNDI lookup result: " + (serviceReference != null ? serviceReference.toString() : null);
  }

}
