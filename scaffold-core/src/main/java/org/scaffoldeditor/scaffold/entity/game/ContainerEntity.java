package org.scaffoldeditor.scaffold.entity.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.joml.Vector3ic;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.WorldMath.SectionCoordinate;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.EntityFactory;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.entity.attribute.BlockAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.ContainerAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.entity.world.BaseSingleBlock;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

import net.querz.nbt.tag.CompoundTag;

/**
 * Compiles into a container, such as a barrel or chest.
 * @author Igrium
 *
 */
public class ContainerEntity extends BaseSingleBlock {
	
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
	private Vector3ic oldPos;

	@Attrib
	protected ContainerAttribute contents = new ContainerAttribute();
	@Attrib
	protected BlockAttribute block = new BlockAttribute(new Block("minecraft:chest"));
	@Attrib(name = "custom_name")
	protected StringAttribute customName = new StringAttribute("");
	@Attrib
	protected StringAttribute lock = new StringAttribute("");
	@Attrib(name = "additional_nbt")
	protected NBTAttribute additionalNBT = new NBTAttribute(new CompoundTag());

	@Override
	public boolean compileWorld(BlockWorld world, boolean full, Set<SectionCoordinate> sections) {
		world.addBlockEntity(getBlockPosition(), compileBlockEntity());
		return super.compileWorld(world, full, sections);
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
		return contents;
	}
	
	public Block getBlock() {
		return block.getValue();
	}
	
	public CompoundTag getBaseNBT() {
		return additionalNBT.getValue();
	}

	@Override
	protected boolean needsRecompiling() {
		return (!getBlock().equals(oldBlock) || !getBlockPosition().equals(oldPos));
	}

	@Override
	protected void onSetAttributes(Map<String, Attribute<?>> updated) {
		super.onSetAttributes(updated);
		oldBlock = getBlock();
		oldPos = getBlockPosition();
	}

	@Override
	public void updateBlocks() {
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/world_container.sdoc", super.getDocumentation());
	}
}
