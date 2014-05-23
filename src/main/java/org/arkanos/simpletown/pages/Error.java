package org.arkanos.simpletown.pages;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.simpletown.controllers.HTMLPrinter;
import org.arkanos.simpletown.controllers.HTTPHandler;

/**
 * Servlet implementation class Error
 */
@WebServlet("/error")
public class Error extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Error() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HTTPHandler.setUpUIHeaders(response);
		
		HTMLPrinter.openHTML(response);

		HTMLPrinter.openMainContainer(response, "Error", null);

		String content = new String();
		content += "<h1>Oops... there was an error.</h1>";
		if (request.getParameter("e") != null  && request.getParameter("e").compareTo("404") == 0) {
			content += "<p>The page you request doesn't really exist.</p>";
		} else {
			content += "<p>Server logs were written. If the SysAdmin didn't die by poisoning on server room fumes, he will get around to it.</p>";
		}
		// TODO add some log entries for the errors
		HTMLPrinter.windowWrap("Error " + request.getParameter("e"), "error", content, response);

		HTMLPrinter.closeMainContainer(response);

		HTMLPrinter.closeHTML(response);
	}

	/*protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO merge and make sure error page covers all methods
		HTMLPrinter.printHTMLConfig(request, response);
		response.getWriter().println("<head>");
		response.getWriter().println(HTMLPrinter.basicHTMLHeaders());
		response.getWriter().println("</head><body>");

		HTMLPrinter.openMainContainer(response.getWriter(), "Error", null);

		String content = new String();
		content += "<h1>Oops... there was an error.</h1>";
		if (request.getParameter("error").compareTo("404") == 0) {
			content += "<p>The page you request doesn't really exist.</p>";
		} else {
			content += "<p>Server logs were written. If the SysAdmin didn't die by poisoning on server room fumes, he will get around to it.</p>";
		}
		// TODO add some log entries for the errors
		response.getWriter().println(HTMLPrinter.windowWrap("Error " + request.getParameter("error"), "error", content));

		HTMLPrinter.closeMainContainer(response.getWriter());

		response.getWriter().println("</body></html>");
	}*/
}
