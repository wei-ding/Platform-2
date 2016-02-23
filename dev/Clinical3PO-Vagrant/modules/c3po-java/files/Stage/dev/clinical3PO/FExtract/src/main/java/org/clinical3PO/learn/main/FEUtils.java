package org.clinical3PO.learn.main;

import java.util.HashMap;
import java.util.Map;

/**
 * Utils class
 * 
 * Is a immutable class. Which cannot be modified by outside class by just accessing the class instance.
 * @author 3129891
 *
 */
final class FEUtils {

	private static FEUtils utils = null;
	private Map<String, String> map = null;

	private FEUtils() {	
	}
	
	private FEUtils(Object[] arr) {

		map = new HashMap<String, String>();
		parseConcpetParams(arr);
	}

	protected static FEUtils getFEUtils(Object[] arr) {
		utils = new FEUtils(arr);
		return utils;
	}

	private void parseConcpetParams(Object[] arr) {

		for(Object obj : arr) {

			String[] array = obj.toString().split("\\s+");
			if(array.length == 2 && map != null) {
				map.put(array[0], array[1].toLowerCase());
			}
		}
	}

	public Map<String, String> getMap() {
		return this.map;
	}
}
