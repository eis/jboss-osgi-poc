package fi.eis.applications.jboss.poc.webapp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

@SuppressWarnings("serial")
@WebServlet("/BundleLookup")
public class LookupAnyBundleServlet extends HttpServlet {
  // Provide logging
  static final Logger log = Logger.getLogger(LookupAnyBundleServlet.class);

  @Resource
  private BundleContext context;

  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse res) throws ServletException, IOException {
    String message = process(req.getParameter("bnd"), req.getParameter("cmd"));
    PrintWriter out = res.getWriter();
    out.print(message);
    out.close();
  }

  private String process(final String bnd, final String cmd) {
    String response = "bundle not available";
    if (bnd != null) {
      Bundle[] bundles = getPackageAdmin().getBundles(bnd, null);
      if (bundles == null || bundles.length != 1) {
        return "Bundle not available: " + bnd;
      }
      Bundle bundle = bundles[0];
      if ("startlevel".equals(cmd)) {
        response = bundle + ": startlevel => "
            + getStartLevel().getBundleStartLevel(bundle);
      } else {
        response = bundle + ": state => " + bundle.getState();
      }
    }
    return response;
  }

  private StartLevel getStartLevel() {
    ServiceReference sref = context.getServiceReference(StartLevel.class
        .getName());
    return (StartLevel) context.getService(sref);
  }

  private PackageAdmin getPackageAdmin() {
    ServiceReference sref = context.getServiceReference(PackageAdmin.class
        .getName());
    return (PackageAdmin) context.getService(sref);
  }
}
