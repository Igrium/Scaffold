package org.scaffoldeditor.scaffold.level.entity.game;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.util.MCEntity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityAdder;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.Rotatable;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.render.MCRenderEntity;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.ListTag;

/**
 * Represents a Minecraft entity in the editor
 * @author Igrium
 *
 */
public class GameEntity extends Rotatable implements TargetSelectable, EntityAdder {
	
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
			map.put("template", new BooleanAttribute(false));
			return map;
		}
	
	@Override
	public void onAdded() {
		super.onAdded();
		updateRenderEntities();
	}
	
	@Override
	public void updateRenderEntities() {
		MCRenderEntity entity = new MCRenderEntity(this, getPosition(), new Vector3f(0, 0, 0),
				new MCEntity(getEntityType(), getNBT()), "entity");
		updateRenderEntities(Collections.singleton(entity));
	}
	
	@Override
	public void onUpdateAttributes(boolean noRecompile) {
		super.onUpdateAttributes(noRecompile);
		updateRenderEntities();
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
	 * Get whether this is a template entity. If this is a template entity, it will
	 * not compile into the world. Instead, it will wait to be spawned via an input.
	 * 
	 * @return Is template?
	 */
	public boolean isTemplate() {
		return (boolean) this.attributes().get("template").getValue();
	}
	
	/**
	 * Set whether this is a template entity. If this is a template entity, it will
	 * not compile into the world. Instead, it will wait to be spawned via an input.
	 * 
	 * @param isTemplate Is template?
	 */
	public void setTemplate(boolean isTemplate) {
		setAttribute("template", new BooleanAttribute(isTemplate));
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
		if (!isTemplate()) {
			CompoundTag nbt = getNBT().clone();
			nbt.putString("id", getEntityType());
			world.addEntity(nbt, this.getPosition().toDouble());
		}
		return true;
	}
	
	/**
	 * Get the command used for spawning the entity
	 * @return
	 */
	public String getSpawnCommand() {
		Vector3f position = getPosition();
		
		// Set rotation
		ListTag<FloatTag> rotArray = new ListTag<>(FloatTag.class);
		rotArray.add(new FloatTag(rotX()));
		rotArray.add(new FloatTag(rotY()));
		
		getNBT().put("Rotation", rotArray);
		getNBT().putString("CustomName","\""+getName()+"\"");
		
		String nbt = "{}";
		try {
			nbt = SNBTUtil.toSNBT(getNBT());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String command = "summon "+getEntityType()+" "+position.x+" "+position.y+" "+position.z+" "+nbt;
		
		getNBT().remove("Rotation");
		getNBT().remove("CustomName");

		return command;
	}
	
	
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		super.compileLogic(datapack);
		return true;
	}

	@Override
	public TargetSelector getTargetSelector() {
		return TargetSelector.fromString("@e[type="+getEntityType()+",name="+getName()+"]");
	}
	
	@Override
	public String getDefaultName() {
		return "entity";
	}

}
