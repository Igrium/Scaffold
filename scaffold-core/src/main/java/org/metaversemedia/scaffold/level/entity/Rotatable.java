package org.metaversemedia.scaffold.level.entity;

import org.metaversemedia.scaffold.level.Level;

/**
 * Defines an entity that can be rotated on the X and Y axis
 * @author Sam54123
 *
 */
public class Rotatable extends Entity {

	public Rotatable(Level level, String name) {
		super(level, name);
		setAttribute("rotX", 0.0f);
		setAttribute("rotY", 0.0f);
	}
	
	/**
	 * Get the entity's X rotation.
	 * @return X Degrees
	 */
	public float rotX() {
		return (float) getAttribute("rotX");
	}
	
	/**
	 * Get the entity's Y rotation.
	 * @return Y Degrees
	 */
	public float rotY() {
		return (float) getAttribute("rotY");
	}
	
	/**
	 * Set the entity's X rotation.
	 * @param deg X Degrees
	 */
	public void setRotX(float deg) {
		setAttribute("rotX", deg);
	}
	
	/**
	 * Set the entity's Y rotation.
	 * @param deg Y Degrees
	 */
	public void setRotY(float deg) {
		setAttribute("rotY", deg);
	}

}
