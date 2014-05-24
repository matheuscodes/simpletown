package org.arkanos.simpletown.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.simpletown.caches.CacheServer;
import org.arkanos.simpletown.controllers.CookieHandler;
import org.arkanos.simpletown.controllers.HTTPHandler;
import org.arkanos.simpletown.controllers.SessionServer;
import org.arkanos.simpletown.controllers.SessionServer.Session;
import org.arkanos.simpletown.logic.Place;

/**
 * Servlet implementation class PlayerAPI
 */
@WebServlet("/player/*")
public class PlayerAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PlayerAPI() {
        super();
		CacheServer.buildAll();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie session_cookie = CookieHandler.searchCookie(CookieHandler.SIMPLETOWN_SESSION, request.getCookies());
		Session s = null;
		if (session_cookie != null) {
			s = SessionServer.getSession(session_cookie.getValue());
			if (s == null) {
				response.sendError(403,"Invalid session.");
				return;
			}
		}
		else{
			response.sendError(403,"Not authenticated.");
			return;
		}
		
		HTTPHandler.setUpAPIHeaders(response);
		
		String reference = request.getRequestURI().toString();
		if(!reference.endsWith("/")) reference += "/";
		reference = reference.substring(reference.lastIndexOf("/player/"));
		reference = reference.replace("/player","");
		
		if(reference.startsWith("/scenario/")){
			Place selected = s.getUser().getLead().getPlace();
			if(selected != null){
				response.getWriter().println(selected.toJSON());
				return;
			}
			else{
				response.sendError(500);
				return;
				//TODO if there is no place defined citizen is at city level.
			}
		}
		
		response.sendError(404, "Player request not found.");
	}
	
	/**
	 * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HTTPHandler.setUpAPIHeaders(response);
		
		String place_reference = request.getRequestURI().toString();
		place_reference = place_reference.substring(place_reference.lastIndexOf("/places/"));
		//TODO maybe not required
		if(!place_reference.endsWith("/")) place_reference += "/";
		place_reference = place_reference.replace("/places/","");
		
		Place selected = CacheServer.getPlaces().getPlace(place_reference);
		if(selected != null){
			//response.getWriter().println(selected.toJSON());
			//TODO
		}
		else{
			//FIXME redirect messes up CSS
			response.sendError(404, "Place not found.");
		}
	}
}