package org.scaffoldeditor.scaffold.level.entity.game;

import org.scaffoldeditor.nbt.NBTStrings;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.Rotatable;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.math.Vector;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.ListTag;

/**
 * Represents a Minecraft entity in the editor
 * @author Igrium
 *
 */
public class GameEntity extends Rotatable implements TargetSelectable {
	
	public static void Register() {
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
	public CompoundTag nbt() {
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
	@SuppressWarnings("deprecation")
	public String getNBTString() {
		return NBTStrings.nbtToString(nbt());
	}
	
	/**
	 * Get the command used for spawning the entity
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getSpawnCommand() {
		Vector position = getPosition();
		
		// Set rotation
		ListTag<FloatTag> rotArray = new ListTag<>(FloatTag.class);
		rotArray.add(new FloatTag(rotX()));
		rotArray.add(new FloatTag(rotY()));
		
		nbt().put("Rotation", rotArray);
		nbt().putString("CustomName","\""+getName()+"\"");
		
		String command = "summon "+getEntityType()+" "+position.X()+" "+position.Y()+" "+position.Z()+" "+NBTStrings.nbtToString(nbt());
		
		nbt().remove("Rotation");
		nbt().remove("CustomName");

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
