package org.scaffoldeditor.scaffold.level.entity;

import org.scaffoldeditor.nbt.math.Vector3f;

/**
 * Represents a box-shaped entity where the bounds can be manually resized.
 * @author Igrium
 */
public interface BrushEntity {
	
	/**
	 * Set the bounds of this brush in world space. Bounds are independent of entity origin.
	 * @param newBounds A two-element array denoting the opposite corners of the brush's bounding box.
	 * @param suppressUpdate If true, block update listeners (compilers, etc) are not triggered.
	 */
	public void setBrushBounds(Vector3f[] newBounds, boolean suppressUpdate);
	
	/**
	 * Get the bounds of this brush in world space.
	 * @return A two-element array denoting the opposite corners of the brush's bounding box.
	 */
	Vector3f[] getBrushBounds();
	
	/**
	 * Determine whether this entity is grid-locked. Being grid-locked means it's a
	 * block entity or some other entity that must be aligned to the block grid.
	 * 
	 * @return Is this entity grid locked?
	 */
	boolean isGridLocked();
}
