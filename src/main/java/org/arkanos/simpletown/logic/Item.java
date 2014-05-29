package org.arkanos.simpletown.logic;

public class Item {
	static final public String TABLE = "item";
	
	static final public String ID_FIELD = "id";
	static final public String NAME_FIELD = "name";
	static final public String DESCRIPTION_FIELD = "description";
	
	
	int id = 0;
	String name = null;
	String description = null;

	public Item(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public String toJSON() {
		String json = "{";
		json += "\""+Item.ID_FIELD+"\":"+this.id+",";
		json += "\""+Item.NAME_FIELD+"\":\""+this.name+"\",";
		json += "\""+Item.DESCRIPTION_FIELD+"\":\""+this.description+"\"";
		json += "}";
		return json;
	}

}
