package org.scaffoldeditor.scaffold.level.entity;

import org.scaffoldeditor.scaffold.math.Vector;

public interface BrushEntity {
	
	/**
	 * Set the bounds of this brush in world space.
	 * @param newBounds A two-element array denoting the opposite corners of the brush's bounding box.
	 */
	public void setBounds(Vector[] newBounds);
	
	/**
	 * Get the bounds of this brush in world space.
	 * @return A two-element array denoting the opposite corners of the brush's bounding box.
	 */
	Vector[] getBounds();
}
