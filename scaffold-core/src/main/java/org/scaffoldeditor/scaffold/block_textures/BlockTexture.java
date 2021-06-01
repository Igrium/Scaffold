package org.scaffoldeditor.scaffold.block_textures;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.math.Vector3d;

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
	 * consistant accross multiple calls to the function.
	 * 
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate.
	 * @return The block at this position, according to the texture.
	 */
	Block blockAt(int x, int y, int z);

	/**
	 * Get whether this texture supports scaling. This should be true for procedural
	 * textures like noise textures, but is likely false for textures based on
	 * pre-determined assets.
	 */
	boolean supportsScaling();
	
	/**
	 * Get the current texture scale.
	 * @return The current texture scale on all three axes.
	 */
	Vector3d getScale();
	
	/**
	 * Set the texture scale. If the texture doesn't support scaling, this does nothing.
	 * @param scale The new texture scale on all three axes.
	 */
	void setScale(Vector3d scale);
	
	/**
	 * Set the texture scale unilaterally. If the texture doesn't support scaling, this does nothing.
	 * <br>
	 * Shortcut for {@link #setScale(Vector3d)}
	 * @param scale The new texture scale.
	 */
	default void setScale(double scale) {
		setScale(new Vector3d(scale, scale, scale));
	}
}
