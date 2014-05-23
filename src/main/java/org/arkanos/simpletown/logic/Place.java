package org.arkanos.simpletown.logic;

import java.util.HashMap;


public class Place {
	
	static public final String PLACE_TABLE = "place";
	
	static public final String ID_FIELD = "id";
	static public final String NAME_FIELD = "name";
	static public final String DESCRIPTION_FIELD = "description";
	static public final String TYPE_FIELD = "type";
	
	static public final String CONNECTION_TABLE = "type";
	
	static public final String CONNECTION_PLACE_ID_FIELD = "place_id";
	static public final String CONNECTION_NEXT_ID_FIELD = "next_place_id";
	static public final String CONNECTION_NAME_FIELD = "connection_name";
	
	public enum Types {
		POI_RESIDENCE, ROOM;
	};
		
	/* *** Common Parts *** */
	int id = 0;
	String name = null;
	String description = null;
	Types type = null;
	HashMap<String,Place> connections = null;
	
	public Place(int id, String name, String description, String type){
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = Types.valueOf(type);
		connections = new HashMap<String,Place>();
	}
	
	public void connect(String where, Place what){		
		connections.put(where, what);
	}
	
	public int getID(){
		return this.id;
	}

}
