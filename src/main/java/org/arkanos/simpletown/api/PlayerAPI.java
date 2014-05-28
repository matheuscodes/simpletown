package org.arkanos.simpletown.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.simpletown.caches.CacheServer;
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
		Session s = SessionServer.getSession(request, response);
		
		if(s == null) return;
		
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
				String saved = selected.getSavedGame(s.getUser().getLead().getID()+"_"+s.getUser().getLead().getDrama());
				if(saved != null){
					response.getWriter().print(saved);
				}
				else{
					response.getWriter().print(selected.getScript(s.getUser().getLead().getDrama()));
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
		Session s = SessionServer.getSession(request, response);
		
		if(s == null) return;
		
		HTTPHandler.setUpAPIHeaders(response);
		
		String reference = request.getRequestURI().toString();
		if(!reference.endsWith("/")) reference += "/";
		reference = reference.substring(reference.lastIndexOf("/player/"));
		reference = reference.replace("/player/","");
		
		Place where = null;
		//TODO move these to constants
		//WARNING: different / handling between get and put
		if(reference.startsWith("script/")){
			reference = reference.replace("script/","");
			where = CacheServer.getPlaces().getPlace(reference);
			String data = null;
			try {
				JSONParser jp = new JSONParser();
				JSONObject jo = (JSONObject) jp.parse(request.getReader());
				data = jo.toJSONString();
			} catch (ParseException e) {
				//TODO verify if right code error
				response.sendError(400, "Something corrupted the data to be saved.");
				return;
			}
			
			CacheServer.getPlaces().saveGame(s.getUser(),where,data);
			//TODO add return codes
			return;
		}
		else {
			where = CacheServer.getPlaces().getPlace(reference);
			
			if(where != null){
				CacheServer.getCitizens().moveCitizen(s.getUser().getLead(),where);
			}
			else{
				//TODO confirm
				response.sendError(400,"Bad request");
			}
			return;
		}
	}
}