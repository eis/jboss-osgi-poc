package fi.eis.applications.jboss.poc.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

/**
 * @url http://localhost:8080/jboss-poc-webapp/HelloWorld
 */
@SuppressWarnings("serial")
@WebServlet("/HelloWorld")
public class HelloWorldServlet extends HttpServlet {

  private static Logger log = Logger.getLogger(HelloWorldServlet.class);

  private MessageService service = null;

  @Resource
  BundleContext context;

  @Override
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);

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

  static String PAGE_HEADER = "<html><head><title>helloworld</title><body>";
  static String PAGE_FOOTER = "</body></html>";

  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
		List<String> bundles = new ArrayList<String>();
		for (Bundle bundle : context.getBundles()) {
			bundles.add(bundle.getSymbolicName() + ":" + bundle.getVersion().toString());
		}
		Collections.sort(bundles);
		List<String> services = new ArrayList<String>();
		try {
			for (ServiceReference ref : context.getAllServiceReferences(null, null)) {
				services.add(context.getService(ref).toString());
				context.ungetService(ref);
			}
		} catch (InvalidSyntaxException e) {
			throw new IllegalStateException(e);
		}
		Collections.sort(services);
		  
	    resp.setContentType("text/html");
	    PrintWriter writer = resp.getWriter();
	    writer.println(PAGE_HEADER);
	    writer.println("<h1>" + (service != null ? service.getMessage() : "null") + "</h1>");
	    writer.println("Using BundleContext looked up in Servlet Context to list available OSGi Bundles:");
		writer.println("<ul>");
	    for (String bundleInfo : bundles) {
	    	writer.print("<li>");
	        writer.print(bundleInfo);
	    	writer.println("</li>");
	    }
		writer.println("</ul>");
		writer.println("<ul>");
	    for (String service : services) {
	    	writer.print("<li>");
	        writer.print(service);
	    	writer.println("</li>");
	    }
		writer.println("</ul>");
	    writer.println(PAGE_FOOTER);
	    writer.close();

  }

}
