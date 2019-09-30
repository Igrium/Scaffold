package org.metaversemedia.scaffold.level.entity;

import org.json.JSONObject;
import org.metaversemedia.scaffold.level.Level;
/**
 * This type of entity can face any of the four cardinal directions,
 * but cannot have arbitrary rotations
 * 
 * @author Sam54123
 *
 */
public class Faceable extends Entity {
	
	/**
	 * Represents a direction a faceable can point
	 * @author Sam54123
	 */
	public enum Direction {
		NORTH, SOUTH, WEST, EAST
	}

	public Faceable(Level level, String name) {
		super(level, name);
		attributes().put("direction", Direction.NORTH);
	}
	
	/* Make sure Direction is an enum, and not a string */
	@Override
	public void onUnserialized(JSONObject object) {
		if (getAttribute("direction").equals("SOUTH")) {
			setAttribute("direction", Direction.SOUTH);
		} else if (getAttribute("direction").equals("WEST")) {
			setAttribute("direction", Direction.WEST);
		} else if (getAttribute("direction").equals("EAST")) {
			setAttribute("direction", Direction.EAST);
		} else {
			setAttribute("direction", Direction.NORTH); // North is default
		}
		
	}
	
}
