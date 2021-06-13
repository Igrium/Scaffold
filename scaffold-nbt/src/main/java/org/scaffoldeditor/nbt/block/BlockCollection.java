package org.scaffoldeditor.nbt.block;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.scaffoldeditor.nbt.math.Vector3d;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.nbt.util.Pair;

import net.querz.nbt.tag.CompoundTag;

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
	 * <b>Implementers should override {@link BlockCollection#hasBlock(int, int, int)}; not this function!</b>
	 */
	default boolean hasBlock(Vector3i vec) {
		return hasBlock(vec.x, vec.y, vec.z);
	}
	
	public default Block blockAt(Vector3i vec) {
		return blockAt(vec.x, vec.y, vec.z);
	}
	
	/**
	 * Get a set of all the locations within this block collection where a block
	 * entity exists.
	 * 
	 * @return Locations with a block entity.
	 */
	default Set<Vector3i> getBlockEntities() {
		return Collections.emptySet();
	}
	
	/**
	 * Obtain all of this collection's entities. THIS COLLECTION IS NOT GUARANTEED
	 * TO BE MUTABLE!
	 * 
	 * @return A collection of pairs, where the first entry is the entity's NBT, and
	 *         the second entry is its position in local space relative to this
	 *         block collection. We keep them seperately because entity positions
	 *         are stored relative to their parent block collection, and it would be
	 *         too difficult to try and keep the NBT data updated.
	 */
	default Collection<Pair<CompoundTag, Vector3d>> getEntities() {
		return Collections.emptySet();
	}
	
	/**
	 * Return the block entity found at a certian position.
	 * 
	 * @param vec Position to search.
	 * @return The block entity's nbt, or <code>null</code> if no entity exists at
	 *         this location.
	 */
	default CompoundTag blockEntityAt(Vector3i vec) {
		return null;
	}
}
