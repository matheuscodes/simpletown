package org.arkanos.simpletown.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.simpletown.caches.CacheServer;
import org.arkanos.simpletown.controllers.HTTPHandler;
import org.arkanos.simpletown.logic.Place;

/**
 * Servlet implementation class PlaceAPI
 */
@WebServlet("/places/*")
public class PlaceAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PlaceAPI() {
        super();
        CacheServer.buildAll();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HTTPHandler.setUpAPIHeaders(response);
		
		String place_reference = request.getRequestURI().toString();
		place_reference = place_reference.substring(place_reference.lastIndexOf("/places/"));
		//TODO maybe not required
		if(!place_reference.endsWith("/")) place_reference += "/";
		place_reference = place_reference.replace("/places/","");
		
		Place selected = CacheServer.getPlaces().getPlace(place_reference);
		if(selected != null){
			response.getWriter().println(selected.toJSON());
		}
		else{
			//FIXME redirect messes up CSS
			response.sendError(404, "Place not found.");
		}
	}
}
