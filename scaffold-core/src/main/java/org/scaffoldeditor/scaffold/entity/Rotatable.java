package org.scaffoldeditor.scaffold.entity;

import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.attribute.FloatAttribute;
import org.scaffoldeditor.scaffold.level.Level;

/**
 * Defines an entity that can be rotated on the X and Y axis
 * @author Igrium
 *
 */
public abstract class Rotatable extends Entity {

	public Rotatable(Level level, String name) {
		super(level, name);
	}

	@Attrib
	private FloatAttribute rotX = new FloatAttribute(0);

	@Attrib
	private FloatAttribute rotY = new FloatAttribute(0);
	
	/**
	 * Get the entity's X rotation.
	 * @return X Degrees
	 */
	public float rotX() {
		return rotX.getValue();
	}
	
	/**
	 * Get the entity's Y rotation.
	 * @return Y Degrees
	 */
	public float rotY() {
		return rotY.getValue();
	}
	
	/**
	 * Set the entity's X rotation.
	 * @param deg X Degrees
	 */
	public void setRotX(float deg) {
		setAttribute("rotX", new FloatAttribute(deg));
	}
	
	/**
	 * Set the entity's Y rotation.
	 * @param deg Y Degrees
	 */
	public void setRotY(float deg) {
		setAttribute("rotY", new FloatAttribute(deg));
	}

}
