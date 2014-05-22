package org.arkanos.simpletown.controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class HTMLPrinter {

	public static void openHTML(HttpServletResponse response) throws IOException {
		response.getWriter().println("<html>");
		response.getWriter().println("<head>");
		response.getWriter().println(HTMLPrinter.basicHTMLHeaders());
		response.getWriter().println("</head>");
	}
	
	public static void closeHTML(HttpServletResponse response) throws IOException {
		response.getWriter().println("</body></html>");
	}

	public static void openMainContainer(PrintWriter writer, String crumbs, String username) {
		writer.println("<div class='main_cointainer_block'>");
		writer.println("<div class='main_cointainer_title'>");
		writer.println("<p> Simpletown </p>");
		writer.println("<p class='crumbs'>" + crumbs + "</p>");
		if (username != null) {
			writer.println("<p class='user_info'> Logged as " + username + ". <a href='Logout'>Logout.</a></p>");
		} else {
			writer.println("<p class='user_info' />");
		}
		writer.println("</div>");
		writer.println("<div class='main_cointainer'>");
	}

	public static void closeMainContainer(PrintWriter writer) {
		writer.println("</div></div>");
	}

	public static String windowWrap(String title, String content_class, String content) {
		String window = new String();
		window += "<div class='window_block " + content_class + "_block'>";
		window += "<div class='window_title'>";
		window += title;
		window += "</div>";
		window += "<div class='window " + content_class + "'>";
		window += content;
		window += "</div></div>";
		return window;
	}

	public static String basicHTMLHeaders() {
		String head = new String();
		head += "<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1' />";
		head += "<link href='./css/general.css' rel='stylesheet' type='text/css'>";
		head += "<link href='./css/global.css' rel='stylesheet' type='text/css'>";
		return head;
	}

}
