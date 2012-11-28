package fi.eis.applications.jboss.poc.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jboss.logging.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

/**
 * A simple stateless session bean.
 * 
 * @author thomas.diesler@jboss.com
 */
@Stateless
@LocalBean
public class SimpleStatelessSessionBean {

  static final Logger log = Logger.getLogger(SimpleStatelessSessionBean.class);

  @Resource
  BundleContext context;

  private MessageService service;

  @PostConstruct
  public void init() {

    final SimpleStatelessSessionBean bean = this;

    ServiceTracker tracker = new ServiceTracker(context,
        MessageService.class.getName(), null) {

      @Override
      public Object addingService(final ServiceReference sref) {
        log.infof("Adding service: %s to %s", sref, bean);
        service = (MessageService) super.addingService(sref);
        return service;
      }

      @Override
      public void removedService(final ServiceReference sref, final Object sinst) {
        super.removedService(sref, service);
        log.infof("Removing service: %s from %s", sref, bean);
        service = null;
      }
    };
    tracker.open();
  }

  public String getMessage() {

    if (service == null)
      return "Service not available";

    return " - EJB - " + service.getMessage();
  }
}