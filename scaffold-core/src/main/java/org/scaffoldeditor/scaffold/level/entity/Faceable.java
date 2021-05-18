package org.scaffoldeditor.scaffold.level.entity;

import org.json.JSONObject;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
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
		setDirection(Direction.NORTH);
	}
	
	/* Make sure Direction is an enum, and not a string */
	@Override
	public void onUnserialized(JSONObject object) {
		if (getAttribute("direction").equals("SOUTH")) {
			setDirection(Direction.SOUTH);
		} else if (getAttribute("direction").equals("WEST")) {
			setDirection(Direction.WEST);
		} else if (getAttribute("direction").equals("EAST")) {
			setDirection(Direction.EAST);
		} else {
			setDirection(Direction.NORTH); // North is default
		}
		
	}
	
	public void setDirection(Direction direction) {
		setAttribute("direction", new StringAttribute(direction.toString()));
	}
	
}
