package org.arkanos.simpletown.controllers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieHandler {
	static public String SIMPLETOWN_SESSION = "simpletown_session";

	static public Cookie searchCookie(String what, Cookie[] where) {
		if (where != null) {
			for (Cookie c : where) {
				if (c.getName().compareTo(what) == 0)
					return c;
			}
		}
		return null;
	}

	static public void deleteCookies(HttpServletRequest request, HttpServletResponse response) {
		for (Cookie c : request.getCookies()) {
			Cookie deleted = new Cookie(c.getName(), null);
			// TODO maybe add -1 instead
			deleted.setMaxAge(0);
			response.addCookie(deleted);
		}
	}

}
