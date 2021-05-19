package org.scaffoldeditor.scaffold.level.entity;

import java.util.HashSet;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.Chunk;
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
	boolean compileWorld(BlockWorld world, boolean full);
	
	/**
	 * Get the block this entity believes should be at a particular location. This function
	 * is called often and must be efficient. If the entity doesn't care what block is in this location,
	 * (eg. it is outside the bounds of the entity), this should return null.
	 * @param coord Location to check, in local space.
	 * @return The block at the requested location. Null if the entity doesn't care what's in this location.
	 */
	Block blockAt(Vector coord);
	
	/**
	 * Get the bounds of this block entity in world space. Used for determining when it has to be recompiled.
	 * @return A two-element array denoting the opposite corners of the entity's bounding box.
	 */
	Vector[] getBounds();
	
	/**
	 * Check if this block entity overlaps a certian area.
	 * @param point1 x1, z1
	 * @param point2 x2, z2
	 * @return Is overlapping?
	 */
	default boolean overlapsArea(float[] point1, float[] point2) {
		// Math reference: https://developer.mozilla.org/en-US/docs/Games/Techniques/3D_collision_detection
		Vector[] bounds = getBounds();
		boolean x = (Math.min(bounds[0].X(), bounds[1].X()) <= Math.max(point1[0], point2[0]) &&
				Math.max(bounds[0].X(), bounds[1].X()) >= Math.min(point1[0], point2[0]));
		
		boolean z = (Math.min(bounds[0].Z(), bounds[1].Z()) <= Math.max(point1[1], point2[1]) &&
				Math.max(bounds[0].Z(), bounds[1].Z()) >= Math.min(point1[1], point2[1]));
		
		return (x && z);
	}
	
	/**
	 * Get a set of all the chunks this entity overlaps with.
	 * <br>
	 * <b>Note:</b> Only returns chunks which have been initialized. When setting dirty chunks, this doesn't matter.
	 * @param world World to search.
	 */
	default Set<ChunkCoordinate> getOverlappingChunks(BlockWorld world) {
		Set<ChunkCoordinate> overlapping = new HashSet<>();
		
		for (ChunkCoordinate c : world.getChunks().keySet()) {
			float[] point1 = new float[] { c.x() * Chunk.WIDTH, c.z() * Chunk.LENGTH };
			float[] point2 = new float[] { point1[0] + Chunk.WIDTH, point1[1] + Chunk.LENGTH };
			
			if (overlapsArea(point1, point2)) {
				overlapping.add(c);
			}
		}
		
		return overlapping;
	}
}
