package org.scaffoldeditor.scaffold.entity.world;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.EntityFactory;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

import net.querz.nbt.tag.CompoundTag;

/**
 * Places a single block into the world.
 * @author Igrium
 */
public class SingleBlock extends BaseSingleBlock {
	
	public static void register() {
		EntityRegistry.registry.put("single_block", new EntityFactory<Entity>() {		
			@Override
			public Entity create(Level level, String name) {
				return new SingleBlock(level, name);
			}
		});
	}

	@Attrib
	protected StringAttribute blockName = new StringAttribute("minecraft:stone");
	@Attrib NBTAttribute blockProperties = new NBTAttribute(new CompoundTag());
	
	public SingleBlock(Level level, String name) {
		super(level, name);
	}
	
	public Block getBlock() {
		return new Block(blockName.getValue(), blockProperties.getValue());
	}
	
	/**
	 * Set the block this entity represents.
	 * @param block Block to set.
	 */
	public void setBlock(Block block) {
		setAttribute("blockName", new StringAttribute(block.getName()));
		setAttribute("blockProperties", new NBTAttribute(block.getProperties()));
	}

	@Override
	protected boolean needsRecompiling() {
		return true;
	}

	@Override
	public void updateBlocks() {
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/single_block.sdoc", super.getDocumentation());
	}
}
