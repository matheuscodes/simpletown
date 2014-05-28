package org.arkanos.simpletown.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Controls the Database connection.
 * 
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
public class Database {
	/** Default host for the database **/
	static private String	HOST		= "localhost:3306";
	/** Default database name **/
	static private String	DATABASE	= "simpletown";
	/** Default database username **/
	static private String	USER		= "root";
	/** Default user password **/
	static private String	PASSWORD	= "1234";
	/** Static connection to the database **/
	static Connection		link		= null;
	
	/**
	 * Simply executes a query in the database.
	 * 
	 * @param q String with the SQL.
	 * @return whether the query could be executed.
	 */
	static public boolean execute(String q) {
		try {
			if ((Database.link == null) || Database.link.isValid(1) || Database.link.isClosed()) {
				Database.initialize();
			}
			Statement query = Database.link.createStatement();
			query.execute(q);
			return true;
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Starts up the database.
	 */
	static synchronized public void initialize() {
		/* For CloudControl, MySQL credentials are set as variables */
		if ((System.getenv("MYSQLS_DATABASE") != null) &&
			(System.getenv("MYSQLS_HOSTNAME") != null) &&
			(System.getenv("MYSQLS_PORT") != null) &&
			(System.getenv("MYSQLS_USERNAME") != null) &&
			(System.getenv("MYSQLS_PASSWORD") != null)) {
			
			String database = System.getenv("MYSQLS_DATABASE");
			String host = System.getenv("MYSQLS_HOSTNAME");
			int port = Integer.valueOf(System.getenv("MYSQLS_PORT"));
			String username = System.getenv("MYSQLS_USERNAME");
			String password = System.getenv("MYSQLS_PASSWORD");
			
			Database.HOST = host + ":" + port;
			Database.DATABASE = database;
			Database.USER = username;
			Database.PASSWORD = password;
		}
		
		try {
			if ((Database.link != null) && Database.link.isValid(1) && !Database.link.isClosed()) return;
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Database.link = DriverManager.getConnection("jdbc:mysql://" + Database.HOST + "/" + Database.DATABASE + "?user=" + Database.USER + "&password=" + Database.PASSWORD);
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Executes a query in the database and returns the results.
	 * 
	 * @param q String with the SQL query.
	 * @return a ResultSet with the results or null if failed.
	 */
	static public ResultSet query(String q) {
		try {
			if ((Database.link == null) || Database.link.isValid(1) || Database.link.isClosed()) {
				Database.initialize();
			}
			Statement query = Database.link.createStatement();
			query.execute(q);
			ResultSet list = query.getResultSet();
			return list;
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Process a string to remove undesired characters.
	 * 
	 * @param s String to be cleaned.
	 * @return clean string to be used in SQL.
	 */
	static public String sanitizeString(String s) {
		if (s == null) return null;
		//TODO this is a basic clean, needs improvement.
		s = s.replace("\"", "\\\"");
		s = s.replace('`', ' ');
		s = s.replace(';', ' ');
		return s;
	}
}
