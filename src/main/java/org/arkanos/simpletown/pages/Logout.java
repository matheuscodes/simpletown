package org.arkanos.simpletown.pages;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.simpletown.caches.CacheServer;
import org.arkanos.simpletown.controllers.CookieHandler;
import org.arkanos.simpletown.controllers.HTMLPrinter;
import org.arkanos.simpletown.controllers.HTTPHandler;
import org.arkanos.simpletown.controllers.SessionServer;
import org.arkanos.simpletown.controllers.SessionServer.Session;

/**
 * Servlet implementation class Logout
 */
@WebServlet("/logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Logout() {
		super();
		CacheServer.buildAll();
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
		
		HTTPHandler.setUpUIHeaders(response);
		
		CookieHandler.deleteCookies(request, response);

		HTMLPrinter.openHTML(response);

		HTMLPrinter.openMainContainer(response, "Logout", null);

		String content = new String();
		content += "<h1>Sucessfully logged out.</h1>";
		content += "<p>All cookies and stored information were deleted.</p>";

		HTMLPrinter.windowWrap("Logged out!", "logout", content, response);

		HTMLPrinter.closeMainContainer(response);

		HTMLPrinter.closeHTML(response);
	}
}
