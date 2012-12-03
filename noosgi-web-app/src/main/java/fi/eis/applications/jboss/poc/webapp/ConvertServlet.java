package fi.eis.applications.jboss.poc.webapp;

import java.io.IOException;

import javax.annotation.Resource;
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

import fi.eis.applications.jboss.poc.osgiservice.api.ConvertService;

@SuppressWarnings("serial")
@WebServlet("/Convert")
public class ConvertServlet extends HttpServlet {
  private static Logger log = Logger.getLogger(ConvertServlet.class);
	
  @Resource
  BundleContext context;
  
  ConvertService service;
	
  @Override
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);

    final ConvertServlet servlet = this;

    ServiceTracker tracker = new ServiceTracker(context,
    	ConvertService.class.getName(), null) {

      @Override
      public Object addingService(final ServiceReference sref) {
        log.infof("Adding service: %s to %s", sref, servlet);
        service = (ConvertService) super.addingService(sref);
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
	  String newMessage = service.convertMessage(req.getParameter("message"));
	  resp.getWriter().write(newMessage);
  }

}
