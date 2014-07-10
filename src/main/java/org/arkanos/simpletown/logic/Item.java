package org.arkanos.simpletown.logic;


public class Item {
	static final public String TABLE = "item";
	
	static final public String ID_FIELD = "id";
	static final public String NAME_FIELD = "name";
	static final public String DESCRIPTION_FIELD = "description";
	static final public String IMAGE_FIELD = "image";
	
	
	int id = 0;
	String name = null;
	String description = null;
	byte[] image = null;

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

	public void setImage(byte[] image) {
		/*this.image = new char[image.length];
		int i = 0;
		for(byte b: image){
			this.image[i++] = (char)b;
		}*/
		this.image = image;
	}
	
	public byte[] getImage(){
		return this.image;
	}

}
