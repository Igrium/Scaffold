package org.scaffoldeditor.scaffold.entity.game;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.joml.Vector3d;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.util.MCEntity;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.EntityFactory;
import org.scaffoldeditor.scaffold.entity.EntityProvider;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.Rotatable;
import org.scaffoldeditor.scaffold.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.render.EntityRenderEntity;
import org.scaffoldeditor.scaffold.render.RenderEntityManager;
import org.scaffoldeditor.scaffold.sdoc.SDoc;
import org.scaffoldeditor.scaffold.util.UUIDUtils;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.ListTag;

/**
 * Represents a Minecraft entity in the editor
 * @author Igrium
 *
 */
public class GameEntity extends Rotatable implements KnownUUID, EntityProvider {
	
	public static void register() {
		EntityRegistry.registry.put("game_entity", new EntityFactory<Entity>() {		
			@Override
			public Entity create(Level level, String name) {
				return new GameEntity(level, name);
			}
		});
	}

	public GameEntity(Level level, String name) {
		super(level, name);
	}

	protected EntityRenderEntity preview;

	@Attrib
	protected StringAttribute entityType = new StringAttribute("minecraft:marker");

	@Attrib
	protected NBTAttribute nbt = new NBTAttribute();

	@Override
	public void updateRenderEntities() {
		super.updateRenderEntities();
		if (preview == null) {
			preview = RenderEntityManager.getInstance().createMC();
			managedRenderEntities.add(preview);
		}
		preview.setMCEntity(new MCEntity(getEntityType(), getNBT()));
		preview.setPosition(getPosition());
	}
	
	/**
	 * Get entity's type.
	 * @return Entity type.
	 */
	public String getEntityType() {
		return ((StringAttribute) getAttribute("entityType")).toString();
	}
	
	/**
	 * Set entity's type.
	 * @param value New type.
	 */
	public void setEntityType(String value) {
		setAttribute("entityType", new StringAttribute(value));
	}
	
	/**
	 * Get the CompoundMap with this entity's NBT.
	 * @return NBT.
	 */
	public CompoundTag getNBT() {
		return ((NBTAttribute) getAttribute("nbt")).getValue();
	}
	
	/**
	 * Get the nbt data of the entity in the format {data}.
	 * @return Nbt data.
	 */
	public String getNBTString() {
		try {
			return SNBTUtil.toSNBT(getNBT());
		} catch (IOException e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	@Override
	public boolean compileGameEntities(BlockWorld world) {
		CompoundTag nbt = getNBT().clone();
		nbt.putString("id", getEntityType());
		
		// Set rotation
		ListTag<FloatTag> rotArray = new ListTag<>(FloatTag.class);
		rotArray.add(new FloatTag(rotX()));
		rotArray.add(new FloatTag(rotY()));
		
		nbt.put("Rotation", rotArray);
		nbt.putIntArray("UUID", UUIDUtils.toIntArray(getUUID()));
		
		world.addEntity(nbt, this.getPosition());
		return true;
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		super.compileLogic(datapack);
		return true;
	}
	
	@Override
	public String getDefaultName() {
		return "entity";
	}

	@Override
	public UUID getUUID() {
		return getLevel().getCompanionUUID(this);
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/game_entity.sdoc", super.getDocumentation());
	}
}
