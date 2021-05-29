package org.scaffoldeditor.nbt.block;

/**
 * Represents an immutable block collection that has a finite amount of blocks and size
 * @author Igrium
 */
public interface SizedBlockCollection extends BlockCollection {
	/**
	 * Get the width (x) of the block collection.
	 * @return Width.
	 */
	public int getWidth();
	
	/**
	 * Get the width (y) of the block collection.
	 * @return Height.
	 */
	public int getHeight();
	
	/**
	 * Get the length (z) of the block collection.
	 * @return Length.
	 */
	public int getLength();
}
