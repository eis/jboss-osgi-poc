package fi.eis.applications.jboss.poc.webapp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.eis.applications.jboss.poc.ejb.SimpleStatelessSessionBean;

/**
 * @url http://localhost:8080/jboss-poc-webapp/ejb
 */
@SuppressWarnings("serial")
@WebServlet(name = "SimpleBeanClientServlet", urlPatterns = { "/ejb" })
public class SessionBeanClientServlet extends HttpServlet {

  @EJB(lookup = "java:global/ejb-no-osgi-0.0.1-SNAPSHOT/SimpleStatelessSessionBean")
  private SimpleStatelessSessionBean bean;

  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse res) throws ServletException, IOException {
    String message = process();
    PrintWriter out = res.getWriter();
    out.println(message);
    out.close();
  }

  private String process() {
    return "Calling SimpleStatelessSessionBean: " + bean.getMessage();
  }
}
