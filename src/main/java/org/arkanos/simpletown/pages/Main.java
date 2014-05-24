package org.arkanos.simpletown.pages;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
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
 * Servlet implementation class Main
 */
@WebServlet({ "/main", "/home", "" })
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Main() {
        super();
		CacheServer.buildAll();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie session_cookie = CookieHandler.searchCookie(CookieHandler.SIMPLETOWN_SESSION, request.getCookies());
		if (session_cookie != null) {
			Session s = SessionServer.getSession(session_cookie.getValue());
			if (s == null) {
				response.sendRedirect("login");
				return;
			}
		}
		else{
			response.sendRedirect("login");
			return;
		}
		
		HTTPHandler.setUpUIHeaders(response);

		HTMLPrinter.openHTML(response);

		HTMLPrinter.openMainContainer(response, "Mainplace", null);

		String content = "<p onclick='SimpletownUIController.moveTo(\"main-st/1/\")'> START </p>";
		
		HTMLPrinter.windowWrap("Go to...", "places_to_go", content, response);

		HTMLPrinter.closeMainContainer(response);

		HTMLPrinter.closeHTML(response);
	}

}
