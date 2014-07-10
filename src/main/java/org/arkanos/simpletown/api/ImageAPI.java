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
 * Servlet implementation class ImageAPI
 */
@WebServlet("/images/*")
public class ImageAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageAPI() {
        super();
		CacheServer.buildAll();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HTTPHandler.setUpAPIHeaders(response);
		
		String item_reference = request.getRequestURI().toString();
		item_reference = item_reference.substring(item_reference.lastIndexOf("/images/item/"));
		//TODO maybe not required
		if(item_reference.endsWith(".png")){
			item_reference = item_reference.substring(0,item_reference.length()-4);
		}
		else{
			response.sendError(400, "No image requested.");
			return;
		}
		
		item_reference = item_reference.replace("/images/item/","");
		try{
			int item_id = Integer.parseInt(item_reference);
			Item selected = CacheServer.getItems().getItem(item_id);
			if(selected != null){
				//response.setCharacterEncoding("");
				response.setContentType("image/png");
				response.getOutputStream().write(selected.getImage());
				//response.getWriter().write(selected.getImage());
			}
			else{
				//FIXME redirect messes up CSS
				response.sendError(404, "Image not found.");
			}
		}
		catch(NumberFormatException e){
			//FIXME redirect messes up CSS
			response.sendError(404, "Item ID is wrong.");
		}
	}

}
