package org.arkanos.simpletown.caches;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.arkanos.simpletown.controllers.Database;
import org.arkanos.simpletown.logic.Item;

public class ItemCache {

	HashMap<String, Item> items = null;

	public ItemCache() {
		super();
		build();
	}

	public synchronized boolean build() {
		int size = 0;
		try {
			ResultSet count = Database.query("SELECT COUNT(*) FROM "+Item.TABLE);
			count.next();
			size = count.getInt(1);
			items = new HashMap<String, Item>(size);
			count.close();

			ResultSet all = Database.query("SELECT * FROM "+Item.TABLE);
			while (all.next()) {
				Item i = new Item(all.getInt(Item.ID_FIELD), all.getString(Item.NAME_FIELD),all.getString(Item.DESCRIPTION_FIELD));
				Blob b = all.getBlob(Item.IMAGE_FIELD);
				if(b != null){
					long s = b.length();
					if(b.getBytes(1, (int)s) != null){
						i.setImage(b.getBytes(1, (int)s));
					}
				}
				items.put(all.getString(Item.ID_FIELD), i);
			}
			all.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public synchronized boolean flush() {
		boolean flag = false;
		if (items != null) {
			items.clear();
			flag = items.isEmpty();
			items = null;
		}
		return flag;
	}
	
	public Item getItem(int item_id) {
		return items.get(""+item_id);
	}

}
