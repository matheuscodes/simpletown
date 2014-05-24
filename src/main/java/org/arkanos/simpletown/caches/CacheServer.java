package org.arkanos.simpletown.caches;


public class CacheServer {

	static CitizenCache citizen_cache = null;
	static PlaceCache place_cache = null;
	//static DramaCache drama_cache = null;
	static UserCache user_cache = null;
	//private static CityCache city_cache;
	
	static synchronized public void buildAll() {
		// TODO add info and prints
		getUsers();
		getCitizens();
		getPlaces();
		//getDramas().build();
		//getCity().build();
	}
	
	static synchronized public void forceBuildAll() {
		// TODO add info and prints
		getUsers().build();
		getCitizens().build();
		getPlaces().build();
		//getDramas().build();
		//getCity().build();
	}

	static synchronized public void flushAll() {
		if (user_cache != null){
			user_cache.flush();
			user_cache = null;
		}
		if (citizen_cache != null){
			citizen_cache.flush();
			citizen_cache = null;
		}
		if (place_cache != null){
			place_cache.flush();
			place_cache = null;
		}
		/*if (drama_cache != null)
			drama_cache.flush();
		if (city_cache != null)
			city_cache.flush();*/
	}

	static public CitizenCache getCitizens() {
		if (citizen_cache == null) {
			getUsers();
			citizen_cache = new CitizenCache();
		}
		return citizen_cache;
	}

	static public PlaceCache getPlaces() {
		if (place_cache == null) {
			place_cache = new PlaceCache();
		}
		return place_cache;
	}
	
	/*
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
