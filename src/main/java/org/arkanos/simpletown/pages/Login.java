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
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		CacheServer.buildAll();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie session_cookie = CookieHandler.searchCookie(CookieHandler.SIMPLETOWN_SESSION, request.getCookies());
		if (session_cookie != null) {
			Session s = SessionServer.getSession(session_cookie.getValue());
			if (s != null) {
				response.sendRedirect("Main");
			}
		}
		
		HTTPHandler.setUpUIHeaders(response);

		HTMLPrinter.openHTML(response);

		HTMLPrinter.openMainContainer(response, "Login", null);

		String content = new String();
		content += "<form action='' method='post' name='login_form'>";
		content += "<table align='center'>";
		content += "<tr>";
		content += "<td width=1%>Username:</td>";
		content += "<td><input id='username' name='username' type='text' /></td>";
		content += "</tr>";
		content += "<tr>";
		content += "<td>Password:</td>";
		content += "<td><input id='password' name='password' type='password' /></td>";
		content += "</tr>";
		content += "<tr>";
		content += "<td></td>";
		content += "<td>";
		if (request.getParameter("error") != null) {
			content += "<p id='small_error'>";
			switch (request.getParameter("error")) {
			case "1":
				content += "Username or password are wrong!";
				break;
			case "2":
				content += "Username missing!";
				break;
			case "3":
				content += "Password missing!";
				break;
			case "4":
				content += "Session expired, please login!";
				break;
			}
			content += "</p>";
		}
		content += "<input type='submit' name='button' value='Login' />";
		content += "</td>";
		content += "</tr>";
		content += "</table>";
		content += "</form>";

		HTMLPrinter.windowWrap("Username and password", "login", content, response);

		HTMLPrinter.closeMainContainer(response);

		HTMLPrinter.closeHTML(response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (request.getParameter("username").isEmpty()) {
				response.sendRedirect("login?error=2");
				return;
			}
			if (request.getParameter("password").isEmpty()) {
				response.sendRedirect("login?error=3");
				return;
			}
			Session session = SessionServer.makeLogin(request.getParameter("username"), request.getParameter("password"), request);
			if (session == null) {
				response.sendRedirect("login?error=1");
				return;
			}
			Cookie c = new Cookie(CookieHandler.SIMPLETOWN_SESSION, session.getKey());
			c.setMaxAge(Session.MAX_AGE);
			response.addCookie(c);

			response.sendRedirect("");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
