package org.scaffoldeditor.scaffold.level.entity.game;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.util.MCEntity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityProvider;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.Rotatable;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.render.MCRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;
import org.scaffoldeditor.scaffold.logic.Datapack;
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
	
	@Override
		public Map<String, Attribute<?>> getDefaultAttributes() {
			Map<String, Attribute<?>> map = super.getDefaultAttributes();
			map.put("entityType", new StringAttribute("minecraft:marker"));
			map.put("nbt", new NBTAttribute(new CompoundTag()));
			return map;
		}
	
	@Override
	public Set<RenderEntity> getRenderEntities() {
		Set<RenderEntity> set = super.getRenderEntities();
		set.add(new MCRenderEntity(this, getPreviewPosition(), new Vector3f(0, 0, 0),
				new MCEntity(getEntityType(), getNBT()), "entity"));
		return set;
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
		
		world.addEntity(nbt, this.getPosition().toDouble());
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

}
