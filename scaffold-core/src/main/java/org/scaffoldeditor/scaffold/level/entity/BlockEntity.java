package org.scaffoldeditor.scaffold.level.entity;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.scaffold.math.Vector;

/**
 * An interface denoting that an entity is able to add blocks to the world upon compilation.
 * @author Sam54123
 */
public interface BlockEntity {
	
	/**
	 * Compile this entity's blocks into the world.
	 * @param world The world to compile into.
	 * @param full Whether or not this is a full compile.
	 * Long operations are only allowed to run if this is true.
	 * @return Success.
	 */
	public boolean compileWorld(BlockWorld world, boolean full);
	
	/**
	 * Get the block this entity believes should be at a particular location. This function
	 * is called often and must be efficient. If the entity doesn't care what block is in this location,
	 * (eg. it is outside the bounds of the entity), this should return null.
	 * @param coord Location to check, in local space.
	 * @return The block at the requested location. Null if the entity doesn't care what's in this location.
	 */
	public Block blockAt(Vector coord);
}
