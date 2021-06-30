package org.scaffoldeditor.scaffold.level.entity.world;

import java.util.HashMap;
import java.util.Map;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
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
	
	public SingleBlock(Level level, String name) {
		super(level, name);
	}
	
	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = new HashMap<>();
		map.put("blockName", new StringAttribute("minecraft:stone"));
		map.put("blockProperties", new NBTAttribute(new CompoundTag()));
		return map;
	}
	
	public Block getBlock() {
		return new Block(((StringAttribute) getAttribute("blockName")).getValue(),
				((NBTAttribute) getAttribute("blockProperties")).getValue());
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
	public void onUpdateBlockAttributes() {
	}
}
