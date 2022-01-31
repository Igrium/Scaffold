package org.scaffoldeditor.nbt.block;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.joml.Vector3dc;
import org.joml.Vector3ic;

import net.querz.nbt.tag.CompoundTag;

/**
 * This represents an immutable group of blocks and tile entities,
 * like a schematic or structure file.
 * @author Igrium
 */
public interface BlockCollection extends Iterable<Vector3ic> {
	
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
	 * <b>Implementers should override {@link BlockCollection#hasBlock(int, int, int)}; not this function!</b>
	 */
	default boolean hasBlock(Vector3ic vec) {
		return hasBlock(vec.x(), vec.y(), vec.z());
	}
	
	public default Block blockAt(Vector3ic vec) {
		return blockAt(vec.x(), vec.y(), vec.z());
	}
	
	/**
	 * Get a set of all the locations within this block collection where a block
	 * entity exists.
	 * 
	 * @return Locations with a block entity.
	 */
	default Set<Vector3ic> getBlockEntities() {
		return Collections.emptySet();
	}
	
	/**
	 * Obtain all of this collection's entities. THIS COLLECTION IS NOT GUARANTEED
	 * TO BE MUTABLE!
	 * 
	 * @return A map where the key is the entity's NBT, and
	 *         the value is its position in local space relative to this
	 *         block collection. We keep them seperately because entity positions
	 *         are stored relative to their parent block collection, and it would be
	 *         too difficult to try and keep the NBT data updated.
	 */
	default Map<CompoundTag, Vector3dc> getEntities() {
		return Collections.emptyMap();
	}
	
	/**
	 * Return the block entity found at a certian position.
	 * 
	 * @param vec Position to search.
	 * @return The block entity's nbt, or <code>null</code> if no entity exists at
	 *         this location.
	 */
	default CompoundTag blockEntityAt(Vector3ic vec) {
		return null;
	}
}
