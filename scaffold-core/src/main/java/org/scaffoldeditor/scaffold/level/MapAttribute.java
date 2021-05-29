package org.scaffoldeditor.scaffold.level;

import java.util.HashMap;

import org.json.JSONObject;

/**
 * Represents an attribute that is a map (editor supported)
 * @author Igrium
 */
public class MapAttribute extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MapAttribute() {
		
	}
	
	public MapAttribute(JSONObject object) {
		for (String k : object.keySet()) {
			this.put(k, object.getString(k));
		}
	}
	
}
