package org.scaffoldeditor.scaffold.block_textures;

import org.scaffoldeditor.nbt.block.Block;

/**
 * <p>
 * A block texture represents a function that can determine which block should
 * be used at a particular location within a brush. For example, a mix between
 * stone varients on a wall for extra detail would be a block texture.
 * </p>
 * <p>
 * <b>Important:</b> Block textures are deterministic. This means that multiple
 * calls to {@link #blockAt} with the same parameters should all return the same
 * result.
 * </p>
 * 
 * @author Igrium
 */
public interface BlockTexture {

	/**
	 * Get the block that this texture determines should be at particular location.
	 * The coordinate space of the input vector is undefined, but it should be
	 * consistant accross multiple calls to the function. Doubles are used to give
	 * extra resolution to functions that upscale the texture.
	 * 
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate.
	 * @return The block at this position, according to the texture.
	 */
	Block blockAt(double x, double y, double z);

	/**
	 * Get whether this texture supports scaling. This should be true for procedural
	 * textures like noise textures, but is likely false for textures based on
	 * pre-determined assets.
	 */
	boolean supportsScaling();
}
