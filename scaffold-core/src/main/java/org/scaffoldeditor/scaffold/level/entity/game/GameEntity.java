package org.scaffoldeditor.scaffold.level.entity.game;

import java.io.IOException;
import java.util.Collections;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.Rotatable;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.render.MCRenderEntity;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.MCEntity;
import org.scaffoldeditor.scaffold.math.Vector;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.ListTag;

/**
 * Represents a Minecraft entity in the editor
 * @author Igrium
 *
 */
public class GameEntity extends Rotatable implements TargetSelectable {
	
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
		attributes().put("entityType", new StringAttribute("minecraft:area_effect_cloud"));
		attributes().put("nbt", new NBTAttribute(new CompoundTag()));
		attributes().put("spawnOnInit", new BooleanAttribute(true));
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
	 * Should this entity spawn on level init?
	 * @return Should spawn on init.
	 */
	public boolean spawnOnInit() {
		return ((BooleanAttribute) getAttribute("spawnOnInit")).getValue();
	}
	
	/**
	 * Set whether this entity should spawn on level init.
	 * @param spawn Should spawn on init.
	 */
	public void setSpawnOnInit(boolean spawn) {
		setAttribute("spawnOnInit", new BooleanAttribute(spawn));
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
	
	/**
	 * Get the command used for spawning the entity
	 * @return
	 */
	public String getSpawnCommand() {
		Vector position = getPosition();
		
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
		
		String command = "summon "+getEntityType()+" "+position.X()+" "+position.Y()+" "+position.Z()+" "+nbt;
		
		getNBT().remove("Rotation");
		getNBT().remove("CustomName");

		return command;
	}
	
	
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		super.compileLogic(datapack);
		
		if (spawnOnInit()) {
			getLevel().initFunction().commands().add(getSpawnCommand());
		}
		
		return true;
	}

	@Override
	public String getTargetSelector() {
		return "@e [type="+getEntityType()+",name="+getName()+"]";
	}
	
	@Override
	public String getDefaultName() {
		return "entity";
	}
}
