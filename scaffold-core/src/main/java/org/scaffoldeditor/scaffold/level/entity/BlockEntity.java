package org.scaffoldeditor.scaffold.level.entity;

import java.util.Collection;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.scaffold.math.Vector;

/**
 * An interface denoting that an entity is able to add blocks to the world upon compilation.
 * @author Sam54123
 */
public interface BlockEntity {
	
	/**
	 * Tell this entity to recompile it's block structure.
	 * Must be called before compileWorld or blockAt is called.
	 * @param full Whether or not this is a full compile. Some operations will take longer if true.
	 * @return Success.
	 */
	public boolean recompile(boolean full);
	
	/**
	 * Output this entity's blocks into the world.
	 * @param world The world to compile into.
	 * @param full Whether or not this is a full compile.
	 * Long operations are only allowed to run if this is true.
	 * @return Success.
	 */
	public boolean compileWorld(BlockWorld world);
	
	/**
	 * Get the block this entity believes should be at a particular location. This function
	 * is called often and must be efficient. If the entity doesn't care what block is in this location,
	 * (eg. it is outside the bounds of the entity), this should return null.
	 * @param coord Location to check, in local space.
	 * @return The block at the requested location. Null if the entity doesn't care what's in this location.
	 */
	public Block blockAt(Vector coord);
	
	/**
	 * Get the chunks which this block entity occupies.
	 * @return Occupied chunks.
	 */
	public Collection<ChunkCoordinate> getOccupiedChunks();
	
}
