package org.arkanos.simpletown.caches;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.arkanos.simpletown.controllers.Database;
import org.arkanos.simpletown.logic.Place;

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
				String street = all.getString(Place.STREET_FIELD);
				int number = all.getInt(Place.NUMBER_FIELD);
				String place =  all.getString(Place.URL_FIELD);
				String fullURL = "";
				fullURL += street;
				fullURL += "/"+number;
				fullURL += "/"+place;
				if(!fullURL.endsWith("/")) fullURL += "/";
				Place p = new Place(fullURL,
									all.getString(Place.NAME_FIELD),
									all.getString(Place.DESCRIPTION_FIELD),
									all.getString(Place.TYPE_FIELD));
				places.put(fullURL, p);
				
				ResultSet scripts = Database.query("SELECT * FROM " + Place.DRAMA_TABLE+
						" WHERE "+Place.DRAMA_STREET_FIELD+" = \""+street+"\""+
						" AND "+Place.DRAMA_NUMBER_FIELD+" = \""+number+"\""+
						" AND "+Place.DRAMA_PLACE_FIELD+" = \""+place+"\"");
				while(scripts != null && scripts.next()){
					p.addScript(scripts.getString(Place.DRAMA_ID_FIELD),scripts.getString(Place.DRAMA_SCRIPT_FIELD));
				}
				scripts.close();
			}
			if(all != null) all.close();
			
			ResultSet connections = Database.query("SELECT "+Place.CONNECTION_NAME_FIELD+", CONCAT("+
											Place.CONNECTION_STREET_FIELD+",\"/\","+
											Place.CONNECTION_NUMBER_FIELD+",\"/\","+
											Place.CONNECTION_PLACE_FIELD+") AS current, CONCAT("+
											Place.CONNECTION_STREET_NEXT_FIELD+",\"/\","+
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

}
