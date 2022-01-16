package org.scaffoldeditor.scaffold.level.entity;

import java.util.HashSet;
import java.util.Set;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3ic;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.WorldMath.SectionCoordinate;

import org.scaffoldeditor.scaffold.math.MathUtils;

/**
 * An interface denoting that an entity is able to add blocks to the world upon compilation.
 * @author Igrium
 */
public interface BlockEntity {
	
	/**
	 * Compile this entity's blocks into the world.
	 * 
	 * @param world    The world to compile into. Note: this may be different than
	 *                 the primary world the entity belongs to.
	 * @param full     Whether or not this is a full compile. Long operations are
	 *                 only allowed to run if this is true.
	 * @param sections Limit compilation to these sections. This is an optimization
	 *                 feature; implementation is fully optional. Blocks placed
	 *                 outside the target sections will be discarded. MAY BE NULL.
	 *                 If it is null, the entire entity should compile.
	 * @return Success.
	 */
	boolean compileWorld(BlockWorld world, boolean full, Set<SectionCoordinate> sections);
	
	/**
	 * Compile this entity's blocks into the world.
	 * @param world The world to compile into.
	 * @param full Whether or not this is a full compile.
	 * Long operations are only allowed to run if this is true.
	 * @return Success.
	 */
	default boolean compileWorld(BlockWorld world, boolean full) {
		return compileWorld(world, full, null);
	}
	
	/**
	 * Get the block this entity believes should be at a particular location. This function
	 * is called often and must be efficient. If the entity doesn't care what block is in this location,
	 * (eg. it is outside the bounds of the entity), this should return null.
	 * @param coord Location to check, in local space.
	 * @return The block at the requested location. Null if the entity doesn't care what's in this location.
	 */
	Block blockAt(Vector3ic coord);
	
	/**
	 * Get the bounds of this block entity in world space. Used for determining when it has to be recompiled.
	 * @return A two-element array denoting the opposite corners of the entity's bounding box. The first 
	 * element should be the minimum coordinate and the second element should be the maximum coordinate.
	 */
	Vector3ic[] getBounds();
	
	/**
	 * Check if this block entity overlaps a certian area.
	 * @param point1 x1, z1
	 * @param point2 x2, z2
	 * @return Is overlapping?
	 */
	default boolean overlapsArea(double[] point1, double[] point2) {
		// Math reference: https://developer.mozilla.org/en-US/docs/Games/Techniques/3D_collision_detection
		Vector3ic[] bounds = getBounds();
		boolean x = (Math.min(bounds[0].x(), bounds[1].x()) <= Math.max(point1[0], point2[0]) &&
				Math.max(bounds[0].x(), bounds[1].x()) >= Math.min(point1[0], point2[0]));
		
		boolean z = (Math.min(bounds[0].z(), bounds[1].z()) <= Math.max(point1[1], point2[1]) &&
				Math.max(bounds[0].z(), bounds[1].z()) >= Math.min(point1[1], point2[1]));
		
		return (x && z);
	}
	
	/**
	 * Check if this block entity overlaps a certian volume.
	 * @param point1 Min point of the volume.
	 * @param point2 Max point of the volume.
	 */
	default boolean overlapsVolume(Vector3dc point1, Vector3dc point2) {
		// Math reference:
		// https://developer.mozilla.org/en-US/docs/Games/Techniques/3D_collision_detection
		Vector3ic[] bounds = getBounds();
		return MathUtils.detectCollision(new Vector3d(bounds[0]), new Vector3d(bounds[1]), point1, point2);
	}
	
	/**
	 * Get the sections that this entity overlaps with.
	 * @return A set of all overlapping sections.
	 */
	default Set<SectionCoordinate> getOverlappingSections() {
		Vector3ic[] bounds = getBounds();
		Set<SectionCoordinate> overlapping = new HashSet<>();
		Vector3ic min = MathUtils.floorVector(new Vector3d(bounds[0]).div(16));
		Vector3ic max = MathUtils.floorVector(new Vector3d(bounds[1]).div(16));
		for (int x = min.x(); x <= max.x(); x++) {
			for (int y = min.y(); y <= max.y(); y++) {
				for (int z = min.z(); z <= max.z(); z++) {
					overlapping.add(new SectionCoordinate(x, y, z));
				}
			}
		}
		
		return overlapping;
	}
	
}
