package org.arkanos.simpletown.caches;


public class CacheServer {

	//static CitizenCache citizen_cache = null;
	//static DialogCache dialog_cache = null;
	//static ConditionCache condition_cache = null;
	//static PlaceCache place_cache = null;
	//static DramaCache drama_cache = null;
	static UserCache user_cache = null;
	//private static CityCache city_cache;

	static public void buildAll() {
		// TODO add info and prints
		//getCitizens().build();
		//getDialogs().build();
		//getConditions().build();
		//getPlaces().build();
		//getDramas().build();
		getUsers().build();
		//getCity().build();
	}

	static public void flushAll() {
		/*if (citizen_cache != null)
			citizen_cache.flush();
		if (dialog_cache != null)
			dialog_cache.flush();
		if (condition_cache != null)
			condition_cache.flush();
		if (place_cache != null)
			place_cache.flush();
		if (drama_cache != null)
			drama_cache.flush();*/
		if (user_cache != null)
			user_cache.flush();/*
		if (city_cache != null)
			city_cache.flush();*/
	}

	/*static public CitizenCache getCitizens() {
		if (citizen_cache == null) {
			citizen_cache = new CitizenCache();
		}
		return citizen_cache;
	}

	static public DialogCache getDialogs() {
		if (dialog_cache == null) {
			dialog_cache = new DialogCache();
		}
		return dialog_cache;
	}

	static public ConditionCache getConditions() {
		if (condition_cache == null) {
			condition_cache = new ConditionCache();
		}
		return condition_cache;
	}

	static public PlaceCache getPlaces() {
		if (place_cache == null) {
			place_cache = new PlaceCache();
		}
		return place_cache;
	}

	static public DramaCache getDramas() {
		if (drama_cache == null) {
			drama_cache = new DramaCache();
		}
		return drama_cache;
	}*/

	static public UserCache getUsers() {
		if (user_cache == null) {
			user_cache = new UserCache();
			// TODO maybe write the dependency better?
			//getCitizens();
		}
		return user_cache;
	}
/*
	static public CityCache getCity() {
		if (city_cache == null) {
			city_cache = new CityCache();
		}
		return city_cache;
	}
*/
}
