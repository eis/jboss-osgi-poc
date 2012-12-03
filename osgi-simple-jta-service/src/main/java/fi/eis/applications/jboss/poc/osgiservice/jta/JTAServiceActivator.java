package fi.eis.applications.jboss.poc.osgiservice.jta;

import org.jboss.logging.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import fi.eis.applications.jboss.poc.osgiservice.api.ConvertService;

public class JTAServiceActivator implements BundleActivator {
  private static Logger log = Logger.getLogger(JTAServiceActivator.class);

  @Override
  public void start(final BundleContext context) throws Exception {
    log.info("Registering my service");

    ConvertService service = new JTAService();

    context.registerService(ConvertService.class.getName(), service, null);
  }

  @Override
  public void stop(final BundleContext context) throws Exception {
    log.info("Unregistering my service");
  }
}
