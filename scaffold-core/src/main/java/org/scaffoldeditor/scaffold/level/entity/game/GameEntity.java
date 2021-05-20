package org.scaffoldeditor.scaffold.level.entity.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import com.github.mryurihi.tbnbt.tag.NBTTag;
import com.github.mryurihi.tbnbt.tag.NBTTagCompound;
import com.github.mryurihi.tbnbt.tag.NBTTagFloat;
import com.github.mryurihi.tbnbt.tag.NBTTagList;
import com.github.mryurihi.tbnbt.tag.NBTTagString;

/**
 * Represents a Minecraft entity in the editor
 * @author Sam54123
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
		attributes().put("nbt", new NBTAttribute(new NBTTagCompound(new HashMap<>())));
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
	public NBTTagCompound nbt() {
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
		return NBTStrings.nbtToString(nbt());
	}
	
	/**
	 * Get the command used for spawning the entity
	 * @return
	 */
	public String getSpawnCommand() {
		Vector position = getPosition();
		
		// Set rotation
		List<NBTTag> rotArray = new ArrayList<NBTTag>();
		rotArray.add(new NBTTagFloat(rotX()));
		rotArray.add(new NBTTagFloat(rotY()));
		
		nbt().put("Rotation", new NBTTagList(rotArray));
		nbt().put("CustomName", new NBTTagString("\""+getName()+"\""));
		
		String command = "summon "+getEntityType()+" "+position.X()+" "+position.Y()+" "+position.Z()+" "+NBTStrings.nbtToString(nbt());
		
		nbt().getValue().remove("Rotation");
		nbt().getValue().remove("CustomName");

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
