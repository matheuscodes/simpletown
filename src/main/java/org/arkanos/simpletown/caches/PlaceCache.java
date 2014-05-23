package org.arkanos.simpletown.caches;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.arkanos.simpletown.controllers.Database;
import org.arkanos.simpletown.logic.Place;

public class PlaceCache implements CacheInterface {
	
	HashMap<String,Place> places;

	@Override
	public boolean build() {
		int size = 0;
		try {
			ResultSet count = Database.query("SELECT COUNT(*) FROM "+Place.PLACE_TABLE);
			count.next();
			size = count.getInt(1);
			places = new HashMap<String, Place>(size);
			count.close();

			ResultSet all = Database.query("SELECT * FROM " + Place.PLACE_TABLE);
			while (all != null && all.next()) {
				Place p = new Place(all.getInt(Place.ID_FIELD),
									all.getString(Place.NAME_FIELD),
									all.getString(Place.DESCRIPTION_FIELD),
									all.getString(Place.TYPE_FIELD));
				places.put(all.getString(Place.ID_FIELD), p);
			}
			all.close();
			
			for(Entry<String, Place> p: places.entrySet()){
				p.getValue();
				ResultSet connections = Database.query("SELECT * FROM " + Place.CONNECTION_TABLE + " WHERE " + Place.CONNECTION_PLACE_ID_FIELD + "=" + p.getValue().getID());
				while (connections != null && connections.next()) {
					Place next = places.get(connections.getInt(Place.CONNECTION_NEXT_ID_FIELD));
					p.getValue().connect(connections.getString(Place.CONNECTION_NAME_FIELD), next);
				}
				connections.close();
			}
			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean flush() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public PlaceCache() {
		super();
		build();
	}

}
