package org.scaffoldeditor.scaffold.block_textures;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.math.Vector3d;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockAttribute;

/**
 * A block texture representing a single block.
 * @author Igrium
 */
public class SingleBlockTexture extends SerializableBlockTexture {
	
	public static final String REGISTRY_NAME = "single_block";
	
	public static void register() {
		BlockTextureRegistry.registry.put(REGISTRY_NAME, new BlockTextureFactory<SingleBlockTexture>() {

			@Override
			public SingleBlockTexture create() {
				return new SingleBlockTexture(new Block("minecraft:stone"));
			}
		});

	}
	
	public SingleBlockTexture(Block block) {
		setBlock(block);
	}
	
	public Block getBlock() {
		return ((BlockAttribute) getAttribute("block")).getValue();
	}
	
	public void setBlock(Block block) {
		setAttribute("block", new BlockAttribute(block));
	}

	@Override
	public Block blockAt(int x, int y, int z) {
		return getBlock();
	}

	@Override
	public boolean supportsScaling() {
		return false;
	}

	@Override
	public Vector3d getScale() {
		return new Vector3d(1,1,1);
	}

	@Override
	public void setScale(Vector3d scale) {}

	@Override
	public String getRegistryName() {
		return REGISTRY_NAME;
	}

}
