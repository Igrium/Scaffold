package org.scaffoldeditor.scaffold.block_textures;

import java.util.Collections;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Block;
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
	public Block blockAt(double x, double y, double z) {
		return getBlock();
	}

	@Override
	public boolean supportsScaling() {
		return false;
	}

	@Override
	public String getRegistryName() {
		return REGISTRY_NAME;
	}

	@Override
	public Set<String> getDefaultAttributes() {
		return Collections.singleton("block");
	}

}
