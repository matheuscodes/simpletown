package org.arkanos.simpletown.controllers;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.simpletown.caches.CacheServer;
import org.arkanos.simpletown.caches.UserCache.User;


public class SessionServer {
	
	public class Session {

		public static final int MAX_AGE = 24 * 60 * 60 * 1000;

		String session_key;
		User user;

		public Session(String key) {
			this.session_key = key;
		}

		public void addUser(User user) {
			this.user = user;
		}

		public String getKey() {
			return session_key;
		}

		/*public Vector<Citizen> getCitizens() {
			return user.getCitizens();
		}*/

		public User getUser() {
			return user;
		}

	}
	
	static HashMap<String, Session> active_sessions;

	static char[] base64 = {
	/** numbers **/
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	/** lower case 1 **/
	'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
	/** lower case 2 **/
	'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
	/** upper case 1 **/
	'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
	/** upper case 2 **/
	'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
	/** extras **/
	'-', '_' };

	private static HashMap<String, Session> getActiveSessions() {
		if (active_sessions == null)
			active_sessions = new HashMap<String, Session>();
		return active_sessions;
	}

	public static Session createSession(String username) {
		String s = convertBase64(System.currentTimeMillis()) + convertBase64(username.hashCode());
		Session newone = (new SessionServer()).new Session(s);
		newone.addUser(CacheServer.getUsers().getUser(username));
		getActiveSessions().put(s, newone);
		return newone;
	}

	public static boolean killSession(String key) {
		if (getActiveSessions().remove(key) != null) {
			return true;
		}
		return false;
	}

	private static String convertBase64(long number) {
		String result = new String();

		if (number == 0)
			return base64[0] + "";

		for (long i = number; i > 0; i /= 64) {
			result = base64[(int) (i % 64)] + result;
		}
		return result;
	}

	public static Session getSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Cookie session_cookie = CookieHandler.searchCookie(CookieHandler.SIMPLETOWN_SESSION, request.getCookies());
		Session s = null;
		if (session_cookie != null) {
			s = getActiveSessions().get(session_cookie.getValue());
			if (s == null) {
				//response.sendError(403,"Invalid session.");
				return null;
			}
		}
		else{
			//response.sendError(403,"Not authenticated.");
			return null;
		}
		return s; 
	}
	
	static private void registerLogin(String username, String ip, String agent, boolean success) {
		// TODO sanitize entries... if "asdsad'; drop table all;" I'm screwed.
		String query = "INSERT INTO user_login_attempts VALUES ";
		query += "(NOW(),'" + username + "','" + ip + "'," + success + ",'" + agent + "');";
		// TODO add proper error handling
		Database.execute(query);
	}

	static public Session makeLogin(String username, String password, HttpServletRequest request) {
		// TODO sanitize entries... if "asdsad'; drop table all;" I'm screwed.
		Session session = null;
		boolean success = false;
		try {
			String query = "SELECT username,password FROM user WHERE username='" + username + "';";
			ResultSet results = Database.query(query);
			//FIXME NPE
			results.next();
			//TODO reference to column names into constants
			String db_pass = results.getString("password");
			if (db_pass.compareTo(password) == 0) {
				session = SessionServer.createSession(results.getString("username"));
				success = true;
			}
			registerLogin(results.getString("username"), request.getRemoteAddr(), request.getHeader("User-Agent"), success);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO add proper error handling
		return session;
	}
}
