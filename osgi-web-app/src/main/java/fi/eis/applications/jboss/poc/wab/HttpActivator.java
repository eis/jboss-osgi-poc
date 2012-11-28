package fi.eis.applications.jboss.poc.wab;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class HttpActivator implements BundleActivator {

  private HttpServiceTracker serviceTracker;

  @Override
  public void start(final BundleContext context) throws Exception {
    serviceTracker = new HttpServiceTracker(context);
    serviceTracker.open();
  }

  @Override
  public void stop(final BundleContext context) throws Exception {
    serviceTracker.close();
    serviceTracker = null;
  }

}