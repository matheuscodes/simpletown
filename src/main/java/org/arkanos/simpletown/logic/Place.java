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
	static public final String CONNECTION_ROAD_FIELD = "road";
	static public final String CONNECTION_NUMBER_FIELD = "number";
	static public final String CONNECTION_PLACE_FIELD = "place";
	static public final String CONNECTION_ROAD_NEXT_FIELD = "road_next";
	static public final String CONNECTION_NUMBER_NEXT_FIELD = "number_next";
	static public final String CONNECTION_PLACE_NEXT_FIELD = "place_next";
	static public final String CONNECTION_BILATERAL = "bilateral";

	public static final String DRAMA_TABLE = "drama_place";
	
	public static final String DRAMA_ROAD_FIELD = "road";
	public static final String DRAMA_NUMBER_FIELD = "number";
	public static final String DRAMA_PLACE_FIELD = "place";
	public static final String DRAMA_ID_FIELD = "drama_id";
	public static final String DRAMA_SCRIPT_FIELD = "script";
	
	static public final String SAVE_TABLE = "citizen_place_save";
	
	static public final String SAVE_ROAD_FIELD = "road";
	static public final String SAVE_NUMBER_FIELD = "number";
	static public final String SAVE_PLACE_FIELD = "place";
	static public final String SAVE_TIME_FIELD = "saved_on";
	static public final String SAVE_DRAMA_FIELD = "drama_id";
	static public final String SAVE_STATE_FIELD = "state";
	public static final String SAVE_CITIZEN_FIELD = "citizen_id";
	
	public enum Types {
		POI_RESIDENCE, ROOM;
	};
	
	public class SavedGame{
		long timestamp = 0;
		String state = null;
		
		public SavedGame(long timestamp,String state){
			this.timestamp = timestamp;
			this.state = state;
		}
		
		public String toJSON(){
			return "{\"saved_on\":"+timestamp+",\"state\":"+state+"}";
		}
	}
	
	public class Address{
		public String road;
		public int number;
		public String place;
		public Address(String road, int number, String place){
			this.road = road;
			this.number = number;
			this.place = place;
		}
	}
		
	/* *** Common Parts *** */
	String fullURL = null;
	String name = null;
	String description = null;
	Types type = null;
	Address address = null;
	HashMap<String,Place> connections = null;
	HashMap<String,String> scripts = null;
	HashMap<String,SavedGame> saves = null;
	
	public Place(String fullURL, String name, String description, String type){
		this.fullURL = fullURL;
		this.name = name;
		this.description = description;
		this.type = Types.valueOf(type);
		connections = new HashMap<String,Place>();
		scripts = new HashMap<String,String>();
		saves = new HashMap<String,SavedGame>();
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
			//FIXME this is no valid JSON.
			return "{}";
		}
		else{
			return result;
		}
	}
	
	public void addSaveGame(String citizen_drama, long timestamp, String state) {
		saves.put(citizen_drama, new SavedGame(timestamp,state));
	}

	public String getSavedGame(String citizen_drama) {
		SavedGame result = saves.get(citizen_drama);
		if(result == null){
			return null;
		}
		else{
			return result.toJSON();
		}
	}
	
	public void setAddress(String road, int number, String place){
		this.address = new Address(road,number,place);
	}
	
	public Address getAddress(){
		return this.address;
	}

}
