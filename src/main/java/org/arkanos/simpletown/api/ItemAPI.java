package org.arkanos.simpletown.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.simpletown.caches.CacheServer;
import org.arkanos.simpletown.controllers.HTTPHandler;
import org.arkanos.simpletown.logic.Item;

/**
 * Servlet implementation class ItemAPI
 */
@WebServlet("/items/*")
public class ItemAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemAPI() {
    	super();
		CacheServer.buildAll();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HTTPHandler.setUpAPIHeaders(response);
		
		String item_reference = request.getRequestURI().toString();
		item_reference = item_reference.substring(item_reference.lastIndexOf("/items/"));
		//TODO maybe not required
		if(item_reference.endsWith("/")) item_reference = item_reference.substring(0,item_reference.length()-1);
		
		item_reference = item_reference.replace("/items/","");
		
		try{
			int item_id = Integer.parseInt(item_reference);
			Item selected = CacheServer.getItems().getItem(item_id);
			if(selected != null){
				response.getWriter().println(selected.toJSON());
			}
			else{
				//FIXME redirect messes up CSS
				response.sendError(404, "Item not found.");
			}
		}
		catch(NumberFormatException e){
			//FIXME redirect messes up CSS
			response.sendError(404, "Item ID is wrong.");
		}
		
	}
}
