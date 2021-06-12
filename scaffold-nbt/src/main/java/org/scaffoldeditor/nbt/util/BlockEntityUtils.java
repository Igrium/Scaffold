package org.scaffoldeditor.nbt.util;

import org.scaffoldeditor.nbt.math.Vector3i;

import net.querz.nbt.tag.CompoundTag;

/**
 * A collection of generic utility functions relating to block entities.
 * @author Igrium
 */
public final class BlockEntityUtils {
	private BlockEntityUtils() {}
	
	/**
	 * Due to the fluidity of block entity locations, especially when it comes to
	 * their reference frame, we don't actually inject the nbt with its location
	 * untill compilation. This method preforms that step.
	 * 
	 * @param ent   NBT to inject into. Should follow the <a href=
	 *              "https://minecraft.fandom.com/wiki/Chunk_format#Block_entity_format">block
	 *              entity format</a>.
	 * @param coord Coordinates to inject.
	 * @see #extractCoordinates
	 */
	public static void injectCoordinates(CompoundTag ent, Vector3i coord) {
		ent.putInt("x", coord.x);
		ent.putInt("y", coord.y);
		ent.putInt("z", coord.z);
		ent.putBoolean("keepPacked", false);
	}
	
	/**
	 * Extract the coordinates from a block entity. See
	 * {@link #injectCoordinates} for reasoning.
	 * 
	 * @param ent NBT to extract coordinates from, removing them from the NBT.
	 *            Should be in <a
	 *            href=https://minecraft.fandom.com/wiki/Chunk_format#Block_entity_format>block
	 *            entity format</a>
	 * @return Extracted coordinates.
	 * @see #injectCoordinates
	 */
	public static Vector3i extractCoordinates(CompoundTag ent) {
		int x = ent.getInt("x");
		int y = ent.getInt("y");
		int z = ent.getInt("z");
		
		ent.remove("x");
		ent.remove("y");
		ent.remove("z");
		
		return new Vector3i(x, y, z);
	}
}
