package fi.eis.applications.jboss.poc.osgiservice;

import org.jboss.logging.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

public class ServiceActivator implements BundleActivator {
  private static Logger log = Logger.getLogger(ServiceActivator.class);

  @Override
  public void start(final BundleContext context) throws Exception {
    log.info("Registering my service");

    MessageService service = new JDBCService();

    context.registerService(MessageService.class.getName(), service, null);
  }

  @Override
  public void stop(final BundleContext context) throws Exception {
    log.info("Unregistering my service");
  }
}
