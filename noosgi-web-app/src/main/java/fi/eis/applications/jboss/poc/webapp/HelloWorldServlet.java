package fi.eis.applications.jboss.poc.webapp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import fi.eis.applications.jboss.poc.osgiservice.api.BundleContextProvider;
import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

/**
 * @url http://localhost:8080/jboss-poc-webapp/HelloWorld
 */
@SuppressWarnings("serial")
@WebServlet("/HelloWorld")
public class HelloWorldServlet extends HttpServlet {

  static String PAGE_HEADER = "<html><head><title>helloworld</title><body>";

  static String PAGE_FOOTER = "</body></html>";

  private static Logger log = Logger.getLogger(HelloWorldServlet.class);

  private MessageService service = null;

  @Override
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);

    // [TODO] should be an injectable resource
    final BundleContext context = BundleContextProvider.getBundleContext();
    final HelloWorldServlet servlet = this;

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

  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html");
    PrintWriter writer = resp.getWriter();
    writer.println(PAGE_HEADER);
    writer.println("<h1>" + service.getMessage() + "</h1>");
    writer.println(PAGE_FOOTER);
    writer.close();
  }

}
