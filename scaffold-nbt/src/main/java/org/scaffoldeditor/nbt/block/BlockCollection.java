package org.scaffoldeditor.nbt.block;

import org.scaffoldeditor.nbt.math.Vector3i;

/**
 * This represents an immutable group of blocks and tile entities,
 * like a schematic or structure file.
 * @author Igrium
 */
public interface BlockCollection extends Iterable<Vector3i> {
	
	/**
	 * Get the the block at a particular location.
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block
	 */
	public Block blockAt(int x, int y, int z);
	
	/**
	 * Check whether a block exists at a particular location.
	 * Default implementation checks if {@link BlockCollection#blockAt} is null.
	 * Implementers are encouraged to override this and make it more efficient.
	 */
	default boolean hasBlock(int x, int y, int z) {
		return blockAt(x, y, z) != null;
	}
	
	/**
	 * Check whether a block exists at a particular location.
	 * <br>
	 * <b>Implementers should override {@link BlockCollection#blockAt(int, int, int)}, not this function!</b>
	 */
	default boolean hasBlock(Vector3i vec) {
		return hasBlock(vec.x, vec.y, vec.z);
	}
	
	public default Block blockAt(Vector3i vec) {
		return blockAt(vec.x, vec.y, vec.z);
	}
	
}
