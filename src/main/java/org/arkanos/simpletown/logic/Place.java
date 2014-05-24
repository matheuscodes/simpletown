package org.arkanos.simpletown.logic;

import java.util.HashMap;
import java.util.Map.Entry;


public class Place {
	
	static public final String PLACE_TABLE = "place";
	
	static public final String STREET_FIELD = "street";
	static public final String NUMBER_FIELD = "number";
	static public final String URL_FIELD = "url";
	static public final String NAME_FIELD = "name";
	static public final String DESCRIPTION_FIELD = "description";
	static public final String TYPE_FIELD = "type";
	
	static public final String CONNECTION_TABLE = "place_connection";
	
	static public final String CONNECTION_NAME_FIELD = "connection_name";
	static public final String CONNECTION_STREET_FIELD = "street";
	static public final String CONNECTION_NUMBER_FIELD = "number";
	static public final String CONNECTION_PLACE_FIELD = "place";
	static public final String CONNECTION_STREET_NEXT_FIELD = "street_next";
	static public final String CONNECTION_NUMBER_NEXT_FIELD = "number_next";
	static public final String CONNECTION_PLACE_NEXT_FIELD = "place_next";
	static public final String CONNECTION_BILATERAL = "bilateral";

	public static final String DRAMA_TABLE = "drama_place";
	
	public static final String DRAMA_STREET_FIELD = "street";
	public static final String DRAMA_NUMBER_FIELD = "number";
	public static final String DRAMA_PLACE_FIELD = "place";
	public static final String DRAMA_ID_FIELD = "drama_id";
	public static final String DRAMA_SCRIPT_FIELD = "script";
	
	
	public enum Types {
		POI_RESIDENCE, ROOM;
	};
		
	/* *** Common Parts *** */
	String fullURL = null;
	String name = null;
	String description = null;
	Types type = null;
	HashMap<String,Place> connections = null;
	HashMap<String,String> scripts = null;
	
	public Place(String fullURL, String name, String description, String type){
		this.fullURL = fullURL;
		this.name = name;
		this.description = description;
		this.type = Types.valueOf(type);
		connections = new HashMap<String,Place>();
		scripts = new HashMap<String,String>();
	}
	
	public void connect(String where, Place what){		
		connections.put(where, what);
	}
	
	public String getFullURL(){
		return this.fullURL;
	}

	public String toJSON() {
		String result = "";
		//TODO modularize the JSON data in a configurable manner
		result += "{\"name\":\""+this.name+"\",";
		result += "\"description\":\""+this.description+"\",";
		result += "\"url\":\""+this.getFullURL()+"\",";
		result += "\"connections\":{";
		for(Entry<String, Place> p: connections.entrySet()){
			result += "\""+p.getKey()+"\":{";
			result += "\"url\": \""+p.getValue().getFullURL()+"\"},";
		}
		if(result.lastIndexOf(",") >= (result.length()-1)){
			result = result.substring(0, result.lastIndexOf(","));
		}
		result += "}}";
		return result;
	}

	public void addScript(String drama, String script) {
		scripts.put(drama, script);
	}

	public String getScript(String drama) {
		String result = scripts.get(drama);
		if(result == null){
			return "{}";
		}
		else{
			return result;
		}
	}

}
