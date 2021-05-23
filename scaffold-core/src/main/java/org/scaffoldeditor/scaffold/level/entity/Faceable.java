package org.scaffoldeditor.scaffold.level.entity;
/**
 * This type of entity can face any of the four cardinal directions,
 * but cannot have arbitrary rotations
 * 
 * @author Igrium
 *
 */
public interface Faceable {
	
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
	
	public void setDirection(String direction);
	
}
