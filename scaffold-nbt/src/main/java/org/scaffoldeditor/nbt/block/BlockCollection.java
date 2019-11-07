package org.scaffoldeditor.nbt.block;

/**
 * This represents an immutable group of blocks and tile entities,
 * like a schematic or structure file.
 * @author Sam54123
 */
public interface BlockCollection extends Iterable<Block> {
	
	/**
	 * Get the the block at a particular location.
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block
	 */
	public Block blockAt(int x, int y, int z);
	
}
