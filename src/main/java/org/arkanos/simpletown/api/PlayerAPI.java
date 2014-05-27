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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
			//FIXME if there is no lead citizen
			Place selected = s.getUser().getLead().getPlace();
			if(selected != null){
				response.getWriter().print(selected.toJSON());
				return;
			}
			else{
				response.sendError(500);
				return;
				//TODO if there is no place defined citizen is at city level.
			}
		}
		else if(reference.startsWith("/script/")){
			reference = reference.replace("/script/", "");
			Place selected = CacheServer.getPlaces().getPlace(reference);
			if(selected != null){
				//TODO move drama to citizen, not user
				String saved = selected.getSavedGame(s.getUser().getLead().getID()+"_"+s.getUser().getDrama());
				if(saved != null){
					response.getWriter().print(saved);
				}
				else{
					response.getWriter().print(selected.getScript(s.getUser().getDrama()));
				}
				return;
			}
			else{
				response.getWriter().print("{}");
				//response.sendError(404,"Place for script not found.");
				return;
			}
		}
		
		response.sendError(404, "Player request not found.");
	}
	
	/**
	 * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HTTPHandler.setUpAPIHeaders(response);
		
		String reference = request.getRequestURI().toString();
		if(!reference.endsWith("/")) reference += "/";
		reference = reference.substring(reference.lastIndexOf("/player/"));
		reference = reference.replace("/player","");
		JSONObject jo = null;
		try {
			JSONParser jp = new JSONParser();
			jo = (JSONObject) jp.parse(request.getReader());
		} catch (ParseException e) {
			//TODO verify code error
			response.sendError(400, "Something corrupted the data to be saved.");
			return;
		}
		
		//TODO write the saved games in the DB
		//TODO save citizen in a place
		
		/*
		Place selected = CacheServer.getPlaces().getPlace(reference);
		if(reference.startsWith("/scenario/")){
			Place selected = s.getUser().getLead().getPlace();
			if(selected != null){
				response.getWriter().print(selected.toJSON());
				return;
			}
			else{
				response.sendError(500);
				return;
				//TODO if there is no place defined citizen is at city level.
			}
		}*/
		
		response.sendError(404, "Player request not found.");
	}
}