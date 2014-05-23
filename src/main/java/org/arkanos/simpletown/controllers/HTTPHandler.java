package org.arkanos.simpletown.controllers;

import javax.servlet.http.HttpServletResponse;

public class HTTPHandler {
	
	
	/**
	 * Defines a common set of header settings for UI pages.
	 * 
	 * @param response to be set.
	 */

	public static void setUpUIHeaders(HttpServletResponse response){
		response.addHeader("Cache-Control", "no-store");
	}
	
	/**
	 * Defines a common set of header settings for API responses.
	 * 
	 * @param response to be set.
	 */

	public static void setUpAPIHeaders(HttpServletResponse response){
		response.addHeader("Cache-Control", "no-store");
		response.setContentType("application/x-json");
	}

}
