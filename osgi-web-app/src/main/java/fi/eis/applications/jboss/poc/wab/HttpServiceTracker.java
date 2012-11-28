package fi.eis.applications.jboss.poc.wab;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class HttpServiceTracker extends ServiceTracker {

  public HttpServiceTracker(final BundleContext context) {
    super(context, HttpService.class.getName(), null);
  }

  @Override
  public Object addingService(final ServiceReference reference) {
    HttpService httpService = (HttpService) super.addingService(reference);
    if (httpService == null)
      return null;

    try {
      System.out.println("Registering servlet at /hi_to_osgi");
      httpService.registerServlet("/hi_to_osgi",
          new AnotherHelloWorldServlet(), null, null);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return httpService;
  }

  @Override
  public void removedService(final ServiceReference reference,
      final Object service) {
    HttpService httpService = (HttpService) service;

    System.out.println("Unregistering /hi_to_osgi");
    httpService.unregister("/hi_to_osgi");

    super.removedService(reference, service);
  }
}