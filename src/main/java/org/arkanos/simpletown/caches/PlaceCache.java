package org.arkanos.simpletown.caches;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.arkanos.simpletown.caches.UserCache.User;
import org.arkanos.simpletown.controllers.Database;
import org.arkanos.simpletown.logic.Citizen;
import org.arkanos.simpletown.logic.Place;
import org.arkanos.simpletown.logic.Place.Address;

public class PlaceCache implements CacheInterface {
	
	HashMap<String,Place> places;

	@Override
	public synchronized boolean build() {
		int size = 0;
		try {
			ResultSet count = Database.query("SELECT COUNT(*) FROM "+Place.PLACE_TABLE);
			count.next();
			size = count.getInt(1);
			places = new HashMap<String, Place>(size);
			count.close();

			ResultSet all = Database.query("SELECT * FROM " + Place.PLACE_TABLE);
			while (all != null && all.next()) {
				String road = all.getString(Place.STREET_FIELD);
				int number = all.getInt(Place.NUMBER_FIELD);
				String place =  all.getString(Place.URL_FIELD);
				String fullURL = "";
				fullURL += road;
				fullURL += "/"+number;
				fullURL += "/"+place;
				if(!fullURL.endsWith("/")) fullURL += "/";
				Place p = new Place(fullURL,
									all.getString(Place.NAME_FIELD),
									all.getString(Place.DESCRIPTION_FIELD),
									all.getString(Place.TYPE_FIELD));
				p.setAddress(road, number, place);
				places.put(fullURL, p);
				
				ResultSet scripts = Database.query("SELECT * FROM " + Place.DRAMA_TABLE+
						" WHERE "+Place.DRAMA_ROAD_FIELD+" = \""+road+"\""+
						" AND "+Place.DRAMA_NUMBER_FIELD+" = \""+number+"\""+
						" AND "+Place.DRAMA_PLACE_FIELD+" = \""+place+"\"");
				while(scripts != null && scripts.next()){
					p.addScript(scripts.getString(Place.DRAMA_ID_FIELD),scripts.getString(Place.DRAMA_SCRIPT_FIELD));
				}
				scripts.close();
			}
			if(all != null) all.close();
			
			ResultSet connections = Database.query("SELECT "+Place.CONNECTION_NAME_FIELD+", CONCAT("+
											Place.CONNECTION_ROAD_FIELD+",\"/\","+
											Place.CONNECTION_NUMBER_FIELD+",\"/\","+
											Place.CONNECTION_PLACE_FIELD+") AS current, CONCAT("+
											Place.CONNECTION_ROAD_NEXT_FIELD+",\"/\","+
											Place.CONNECTION_NUMBER_NEXT_FIELD+",\"/\","+
											Place.CONNECTION_PLACE_NEXT_FIELD+") AS next, "+
											Place.CONNECTION_BILATERAL +
											" FROM " + Place.CONNECTION_TABLE);
			while (connections != null && connections.next()) {
				String current = connections.getString("current");
				if(!current.endsWith("/")) current += "/";
				Place from = places.get(current);
				String next = connections.getString("next");
				if(!next.endsWith("/")) next += "/";
				Place to = places.get(next);
				//FIXME NPE in "from" whenever there are cache building issues.
				from.connect(connections.getString(Place.CONNECTION_NAME_FIELD), to);
				if(connections.getBoolean(Place.CONNECTION_BILATERAL)){
					to.connect(connections.getString(Place.CONNECTION_NAME_FIELD), from);
				}
			}
			if(connections != null) connections.close();
			
			
			ResultSet saves = Database.query("SELECT CONCAT("+
					Place.SAVE_ROAD_FIELD+",\"/\","+
					Place.SAVE_NUMBER_FIELD+",\"/\","+
					Place.SAVE_PLACE_FIELD+") AS address, "+
					Place.SAVE_TIME_FIELD + ","+
					Place.SAVE_DRAMA_FIELD + ","+
					Place.SAVE_CITIZEN_FIELD + ","+
					Place.SAVE_STATE_FIELD + 
					" FROM " + Place.SAVE_TABLE);
			while (saves != null && saves.next()) {
				String address = saves.getString("address");
				
				if(!address.endsWith("/")) address += "/";
				long when = saves.getDate(Place.SAVE_TIME_FIELD).getTime();
				Place where = places.get(address);
				String what = saves.getString(Place.SAVE_STATE_FIELD);
				
				if(where != null){
					where.addSaveGame(saves.getString(Place.SAVE_CITIZEN_FIELD)+"_"+saves.getString(Place.SAVE_DRAMA_FIELD), when, what);
				}
			}
			if(saves != null) saves.close();
			
			
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public synchronized boolean flush() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public PlaceCache() {
		super();
		build();
	}

	public Place getPlace(String reference) {
		if(places != null){
			return places.get(reference);
		}
		return null;
	}

	public void saveGame(User who, Place where, String data) {
		if(data == null) return;
		
		Address a = where.getAddress();
		Citizen lead = who.getLead(); 
		String query = null;
		if(where.getSavedGame(lead.getID()+"_"+lead.getDrama()) != null){
			query = "UPDATE "+Place.SAVE_TABLE+" SET "+
					Place.SAVE_STATE_FIELD + " =  \"" + Database.sanitizeString(data) + "\","+
					Place.SAVE_DRAMA_FIELD + " =  " + lead.getDrama() + ","+
					Place.SAVE_TIME_FIELD + " =  NOW() "+ //TODO is now() required?
					"WHERE "+ Place.SAVE_CITIZEN_FIELD + " = "+lead.getID()+
					" AND "+Place.SAVE_ROAD_FIELD + " =  \"" + a.road + "\""+
					" AND "+Place.SAVE_NUMBER_FIELD + " =  " + a.number +
					" AND "+Place.SAVE_PLACE_FIELD + " =  \"" + a.place + "\"";
		}
		else{
			query = "INSERT INTO " + Place.SAVE_TABLE + "("+
					Place.SAVE_CITIZEN_FIELD+","+
					Place.SAVE_ROAD_FIELD+","+
					Place.SAVE_NUMBER_FIELD+","+
					Place.SAVE_PLACE_FIELD+","+
					Place.SAVE_STATE_FIELD+","+
					Place.SAVE_DRAMA_FIELD+") "+
					"VALUES ("+
					lead.getID()+",\""+
					a.road+"\","+
					a.number+",\""+
					a.place+"\",\""+
					Database.sanitizeString(data)+"\","+
					lead.getDrama()+")";
		}
		
		boolean executed = Database.execute(query);
		if(executed){
			where.addSaveGame(lead.getID()+"_"+lead.getDrama(), System.currentTimeMillis(), data);
		}
		
	}

}
