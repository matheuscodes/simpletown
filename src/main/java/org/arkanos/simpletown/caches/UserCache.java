package org.arkanos.simpletown.caches;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import org.arkanos.simpletown.controllers.Database;
import org.arkanos.simpletown.logic.Citizen;

public class UserCache implements CacheInterface {
	
	public class User {

		int id = 0;
		String username = null;
		String name = null;

		Vector<Citizen> active = null;
		Citizen lead = null;

		public User(String username, String name) {
			this.username = username;
			this.name = name;
		}

		public void addCitizen(Citizen which) {
			//TODO problem when flushing reading everyone again.
			getCitizens().add(which);
		}
		
		public void setLead(Citizen which){
			this.lead = which;
		}
		
		public Citizen getLead(){
			return this.lead;
		}

		public Vector<Citizen> getCitizens() {
			if (active == null)
				active = new Vector<Citizen>();
			return active;
		}

		public int getID() {
			return id;
		}

		public String getName() {
			return name;
		}
	}

	HashMap<String, User> users = null;

	public UserCache() {
		super();
		build();
	}

	public synchronized boolean build() {
		int size = 0;
		try {
			ResultSet count = Database.query("SELECT COUNT(*) FROM user");
			count.next();
			size = count.getInt(1);
			users = new HashMap<String, User>(size);
			count.close();

			ResultSet all = Database.query("SELECT * FROM user");
			while (all.next()) {
				User u = new User(all.getString("username"), all.getString("name"));
				users.put(all.getString("username"), u);
			}
			all.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public synchronized boolean flush() {
		// TODO Auto-generated method stub
		return false;
	}

	public User getUser(String username) {
		return users.get(username);
	}

}
