package org.arkanos.simpletown.caches;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.arkanos.simpletown.controllers.Database;
import org.arkanos.simpletown.logic.Citizen;
import org.arkanos.simpletown.logic.Citizen.Attribute;
import org.arkanos.simpletown.logic.Place;

public class CitizenCache implements CacheInterface {

	// TODO better structure (will get sparse)
	HashMap<String, Citizen> citizens = null;

	public CitizenCache() {
		super();
		CacheServer.getUsers(); //dependency
		CacheServer.getPlaces(); //dependency
		build();
	}

	public synchronized boolean flush() {
		boolean flag = false;
		if (citizens != null) {
			citizens.clear();
			flag = citizens.isEmpty();
			citizens = null;
		}
		return flag;
	}

	public synchronized boolean build() {
		int size = 0;
		try {
			ResultSet count = Database.query("SELECT COUNT(*) FROM "+Citizen.TABLE);
			count.next();
			size = count.getInt(1);
			citizens = new HashMap<String, Citizen>(size);
			count.close();
			
			ResultSet all = Database.query("SELECT * FROM "+Citizen.TABLE+
					" LEFT JOIN "+Citizen.ASPECTS_TABLE+" a "+
					" ON "+Citizen.ID_FIELD+" = a."+Citizen.ASPECTS_ID_FIELD+
					" LEFT JOIN "+Citizen.USER_TABLE+" u "+
					" ON "+Citizen.ID_FIELD+" = u."+Citizen.USER_CITIZEN_ID_FIELD);
			
			while (all.next()) {
				Citizen newone = null;
				String name = all.getString(Citizen.NAME_FIELD);
				String last_name = all.getString(Citizen.LAST_NAME_FIELD);
				byte[] attributes = new byte[Attribute.values().length];
				attributes[Attribute.AGILITY.ordinal()] = all.getByte(Attribute.AGILITY.toString());
				attributes[Attribute.BEARING.ordinal()] = all.getByte(Attribute.BEARING.toString());
				attributes[Attribute.CHARISMA.ordinal()] = all.getByte(Attribute.CHARISMA.toString());
				attributes[Attribute.INTELLECT.ordinal()] = all.getByte(Attribute.INTELLECT.toString());
				attributes[Attribute.MIGHT.ordinal()] = all.getByte(Attribute.MIGHT.toString());
				attributes[Attribute.VIGOR.ordinal()] = all.getByte(Attribute.VIGOR.toString());
				attributes[Attribute.PERCEPTION.ordinal()] = all.getByte(Attribute.PERCEPTION.toString());
				attributes[Attribute.WITS.ordinal()] = all.getByte(Attribute.WITS.toString());
				newone = new Citizen(all.getInt(Citizen.ID_FIELD), name, last_name, attributes);
				citizens.put(all.getString(Citizen.ID_FIELD), newone);
				byte behavior = all.getByte(Citizen.ASPECTS_BEHAVIOUR_FIELD);
				byte state_of_mind = all.getByte(Citizen.ASPECTS_STATE_OF_MIND_FIELD);
				byte bonding = all.getByte(Citizen.ASPECTS_BONDING_FIELD);
				newone.setAspects(behavior, state_of_mind, bonding);
				// TODO optimize, avoid the "gets" and use variable
				if (all.getInt(Citizen.USER_ID_FIELD) != 0) {
					CacheServer.getUsers().getUser(all.getInt(Citizen.USER_ID_FIELD)).addCitizen(newone);
					String address = all.getString(Citizen.USER_STREET_FIELD);
					address += "/"+all.getString(Citizen.USER_NUMBER_FIELD);
					address += "/"+all.getString(Citizen.USER_PLACE_FIELD);
					if(!address.endsWith("/")) address += "/";
					Place where = CacheServer.getPlaces().getPlace(address);
					if(where != null){
						newone.setPlace(where);
					}
					if(all.getBoolean(Citizen.LEAD_ROLE_FIELD)){
						CacheServer.getUsers().getUser(all.getInt(Citizen.USER_ID_FIELD)).setLead(newone);
					}
				}
			}
			all.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/*
	public Vector<Citizen> getAllCitizens() {
		Vector<Citizen> results = new Vector<Citizen>();
		for (Map.Entry<String, Citizen> sc : NPCs.entrySet()) {
			results.add(sc.getValue());
		}
		return results;
	}*/

}
