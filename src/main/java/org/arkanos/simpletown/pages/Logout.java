package org.arkanos.simpletown.pages;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.simpletown.controllers.CookieHandler;
import org.arkanos.simpletown.controllers.HTMLPrinter;
import org.arkanos.simpletown.controllers.SessionServer;
import org.arkanos.simpletown.controllers.SessionServer.Session;

/**
 * Servlet implementation class Logout
 */
@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Logout() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Session session = SessionServer.checkLogin(request, response);
		if (session != null) {
			SessionServer.killSession(session.getKey());
		}
		CookieHandler.deleteCookies(request, response);

		HTMLPrinter.openHTML(response);

		response.getWriter().println("<head>");
		response.getWriter().println(HTMLPrinter.basicHTMLHeaders());
		response.getWriter().println("</head><body>");

		HTMLPrinter.openMainContainer(response.getWriter(), "Logout", null);

		String content = new String();
		content += "<h1>Sucessfully logged out.</h1>";
		content += "<p>All cookies and stored information were deleted.</p>";

		response.getWriter().println(HTMLPrinter.windowWrap("Logged out!", "logout", content));

		HTMLPrinter.closeMainContainer(response.getWriter());

		HTMLPrinter.closeHTML(response);
	}
}
