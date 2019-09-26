package org.metaversemedia.scaffold.nbt;

/**
 * This represents an immutable group of blocks and tile entities,
 * like a schematic or structure file.
 * @author Sam54123
 */
public interface BlockCollection {
	/**
	 * Get the name of the block at a particular location.
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block name
	 */
	public String blockAt(int x, int y, int z);
	
	
}
