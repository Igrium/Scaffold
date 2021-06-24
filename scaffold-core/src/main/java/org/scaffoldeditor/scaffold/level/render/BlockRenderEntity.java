package org.scaffoldeditor.scaffold.level.render;

import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.entity.Entity;

/**
 * Renders a collection of blocks as a holographic projection in the world. Does
 * not have an equivalent in Vanilla Minecraft; only use for block placement
 * previews.
 * 
 * @author Igrium
 */
public class BlockRenderEntity extends RenderEntity {
	
	private final BlockCollection blocks;
	
	/**
	 * Create a block render entity.
	 * 
	 * @param entity     Owning scaffold entity.
	 * @param blocks     The block collection to render.
	 * @param position   World position of the render entity (root of the block
	 *                   collection).
	 * @param rotation   Pitch/yaw/roll of the entity.
	 * @param identifier The unique identifier of this render entity, used to keep
	 *                   track of it in relation to its editor-side representation.
	 *                   Only one instance of this string may exist per Scaffold
	 *                   entity. Different Scaffold entities may share identifiers.
	 */
	public BlockRenderEntity(Entity entity, BlockCollection blocks, Vector3f position, Vector3f rotation, String identifier) {
		super(entity, position, rotation, identifier);
		this.blocks = blocks;
	}
	
	public BlockCollection getBlocks() {
		return blocks;
	}
	
	@Override
	public String toString() {
		return super.toString() + " block_collection: " + blocks.toString();
	}
}
