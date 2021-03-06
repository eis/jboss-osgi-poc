package fi.eis.applications.jboss.poc.wab;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

@SuppressWarnings("serial")
public class AnotherHelloWorldServlet extends HttpServlet {

  private static Logger log = Logger.getLogger(AnotherHelloWorldServlet.class);

  
  private MessageService service = null;
  private BundleContext context;

  @Override
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);
    
    context = (BundleContext) config.getServletContext().getAttribute("osgi-bundlecontext");

    final AnotherHelloWorldServlet servlet = this;

    ServiceTracker tracker = new ServiceTracker(context,
        MessageService.class.getName(), null) {

      @Override
      public Object addingService(final ServiceReference sref) {
        log.infof("Adding service: %s to %s", sref, servlet);
        service = (MessageService) super.addingService(sref);
        return service;
      }

      @Override
      public void removedService(final ServiceReference sref, final Object sinst) {
        super.removedService(sref, service);
        log.infof("Removing service: %s from %s", sref, servlet);
        service = null;
      }
    };
    tracker.open();
  }

  static String PAGE_HEADER = "<html><head><title>helloworld</title><body>";
  static String PAGE_FOOTER = "</body></html>";

  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html");
    PrintWriter writer = resp.getWriter();
    writer.println(PAGE_HEADER);
    writer.println("<h1>" + service.getMessage() + "</h1>");
    writer.println("<h1>Hi!</h1>");
    writer.println("<h2>with OSGi!</h2>");
    writer.println(PAGE_FOOTER);
    writer.close();
  }

}