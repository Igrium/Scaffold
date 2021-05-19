package org.scaffoldeditor.scaffold.level.entity;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
/**
 * This type of entity can face any of the four cardinal directions,
 * but cannot have arbitrary rotations
 * 
 * @author Igrium
 *
 */
public class Faceable extends Entity {
	
	/**
	 * Represents a direction a faceable entity can point
	 * @author Igrium
	 */
	public static class Direction {
		public static String NORTH = "north";
		public static String SOUTH = "south";
		public static String EAST = "east";
		public static String WEST = "west";
	}

	public Faceable(Level level, String name) {
		super(level, name);
		setDirection(Direction.NORTH);
	}
	
	public void setDirection(String direction) {
		setAttribute("direction", new StringAttribute(direction), true);
	}
	
}
