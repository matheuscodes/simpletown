package org.arkanos.simpletown.logic;


public class Citizen {
	
	static public final String TABLE = "citizen";
	
	static public final String ID_FIELD = "id";
	static public final String USERNAME_FIELD = "username";
	static public final String NAME_FIELD = "name";
	static public final String LAST_NAME_FIELD = "last_name";
	static public final String LEAD_ROLE_FIELD = "lead_role";
	
	public static final String PLAYING_TABLE = "citizen_playing";
	
	static public final String PLAYING_BEHAVIOUR_FIELD = "behaviour";
	static public final String PLAYING_STATE_OF_MIND_FIELD = "state_of_mind";
	static public final String PLAYING_BONDING_FIELD = "bonding";
	static public final String PLAYING_CITIZEN_ID_FIELD = "citizen_id";
	static public final String PLAYING_ROAD_FIELD = "road";
	static public final String PLAYING_NUMBER_FIELD = "number";
	static public final String PLAYING_PLACE_FIELD = "place";
	
	
	public enum Attribute {
		AGILITY, BEARING, CHARISMA, INTELLECT, MIGHT, VIGOR, PERCEPTION, WITS;
	
		static public byte MIN_ATTRIBUTE_VALUE = 0;
		static public byte MAX_ATTRIBUTE_VALUE = 100;
	
		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	}
	
	public static final int DEFAULT_ATTRIBUTE_VALUE = 40;
	public static final int MAX_ATTRIBUTE_SUM = DEFAULT_ATTRIBUTE_VALUE * 8;
	static public final byte MIN_ASPECT_VALUE = -100;
	static public final byte MAX_ASPECT_VALUE = 100;
	static public final int MEMORY_RATE = 5;
	static public final float WEIGHT_RATE = 2.0f;

	int id = 0;

	String name = null;

	String last_name = null;

	byte[] attributes = null;

	byte behavior = 0;
	byte state_of_mind = 0;
	byte bonding = 0;
	private Place place;

	//Vector<Skill> skills;

	public Citizen(int id, String name, String last_name, byte[] attributes) {
		this.name = name;
		this.last_name = last_name;
		this.attributes = attributes;
	}

	public byte getBehavior() {
		return behavior;
	}

	public byte getStateOfMind() {
		return state_of_mind;
	}

	public byte getBonding() {
		return bonding;
	}

	public byte getAttribute(Attribute which) {
		return attributes[which.ordinal()];
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getLastName() {
		return last_name;
	}

	public int getMaxMemories() {
		return attributes[Attribute.INTELLECT.ordinal()] * MEMORY_RATE;
	}

	public float getCarryCapacity() {
		return attributes[Attribute.MIGHT.ordinal()] * WEIGHT_RATE;
	}

	public void setAspects(byte behavior, byte state_of_mind, byte bonding) {
		this.behavior = behavior;
		this.state_of_mind = state_of_mind;
		this.bonding = bonding;
	}

	public byte[] getAttributes() {
		return attributes;
	}

	public String describeMood() {
		if (behavior > MAX_ASPECT_VALUE / 2) {
			return "patiently";
		}
		if (state_of_mind > MAX_ASPECT_VALUE / 2) {
			return "calmly";
		}
		if (bonding > MAX_ASPECT_VALUE / 2) {
			return "warmly";
		}
		if (behavior < MIN_ASPECT_VALUE / 2) {
			return "impatiently";
		}
		if (state_of_mind < MIN_ASPECT_VALUE / 2) {
			return "desperately";
		}
		if (bonding < MIN_ASPECT_VALUE / 2) {
			return "coldly";
		}
		return "";
	}

	public void setPlace(Place where) {
		this.place = where;
	}
	
	public Place getPlace(){
		return this.place;
	}
}
