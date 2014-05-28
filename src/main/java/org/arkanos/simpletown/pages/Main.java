package org.arkanos.simpletown.pages;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.simpletown.caches.CacheServer;
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
		HTTPHandler.setUpUIHeaders(response);
		
		Session s = SessionServer.getSession(request,response);
		if(s == null) {
			response.sendRedirect("login");
			return;
		}

		HTMLPrinter.openHTML(response);
		
		HTMLPrinter.openMainContainer(response, s.getUser().getLead().getName()+" "+s.getUser().getLead().getLastName(), null);

		HTMLPrinter.windowWrap("Go through...", "place_navigation", "", response);
		HTMLPrinter.windowWrap("People in the room...", "place_citizens", "", response);
		HTMLPrinter.windowWrap("Dialog", "dialog", "", response);
		
		String content = "<p onclick='controller.moveTo(\"main-st/1/\")'> START </p>";
		HTMLPrinter.windowWrap("Story", "citizen_story", content, response);

		HTMLPrinter.closeMainContainer(response);
		
		response.getWriter().println("<script>controller.redoLayout();</script>");

		HTMLPrinter.closeHTML(response);
	}

}
