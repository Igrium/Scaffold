package org.scaffoldeditor.scaffold.level.entity.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.ContainerAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.entity.world.BaseBlockEntity;

import net.querz.nbt.tag.CompoundTag;

/**
 * Compiles into a container, such as a barrel or chest.
 * @author Igrium
 *
 */
public class ContainerEntity extends BaseBlockEntity {
	
	public static void register() {
		EntityRegistry.registry.put("world_container", new EntityFactory<ContainerEntity>() {
			@Override
			public ContainerEntity create(Level level, String name) {
				return new ContainerEntity(level, name);
			}
		});
	}
	
	public static final Map<String, String> entityIDs = new HashMap<>();

	public ContainerEntity(Level level, String name) {
		super(level, name);
	}
	
	private Block oldBlock;
	private Vector3i oldPos;
	
	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> def = new HashMap<>();
		def.put("contents", new ContainerAttribute());
		def.put("block", new BlockAttribute(new Block("minecraft:chest")));
		def.put("custom_name", new StringAttribute(""));
		def.put("lock", new StringAttribute(""));
		def.put("additional_nbt", new NBTAttribute(new CompoundTag()));
		return def;
	}

	@Override
	public boolean compileWorld(BlockWorld world, boolean full, Set<SectionCoordinate> sections) {
		Vector3i pos = getBlockPosition();
		world.setBlock(pos.x, pos.y, pos.z, getBlock(), this);
		
		world.addBlockEntity(pos, compileBlockEntity());
		
		return true;
	}
	
	public CompoundTag compileBlockEntity() {
		String blockName = getBlock().getName();
		CompoundTag ent = getBaseNBT().clone();
		ent.put("Items", getContents().getValue());
		ent.putString("Lock", (String) getAttribute("lock").getValue());
		
		String id = entityIDs.get(blockName);
		ent.putString("id", id == null ? blockName : id);
		
		String name = (String) getAttribute("custom_name").getValue();
		if (name.length() > 0) {
			ent.putString("CustomName", '"'+name+'"');
		}
		
		return ent;
	}
	
	public ContainerAttribute getContents() {
		return (ContainerAttribute) getAttribute("contents");
	}
	
	public Block getBlock() {
		return (Block) getAttribute("block").getValue();
	}
	
	public CompoundTag getBaseNBT() {
		return (CompoundTag) getAttribute("additional_nbt").getValue();
	}

	@Override
	public Block blockAt(Vector3i coord) {
		return getBlock();
	}

	@Override
	public Vector3i[] getBounds() {
		return new Vector3i[] { getBlockPosition(), getBlockPosition() };
	}

	@Override
	protected boolean needsRecompiling() {
		return (!getBlock().equals(oldBlock) || !getBlockPosition().equals(oldPos));
	}
	
	@Override
	public void onUpdateAttributes(boolean noRecompile) {
		super.onUpdateAttributes(noRecompile);
		oldBlock = getBlock();
		oldPos = getBlockPosition();
	}

	@Override
	public void onUpdateBlockAttributes() {
	}

}
