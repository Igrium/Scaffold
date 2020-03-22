package org.scaffoldeditor.scaffold.level.entity.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.scaffoldeditor.nbt.NBTStrings;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Rotatable;
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

	public GameEntity(Level level, String name) {
		super(level, name);
		attributes().put("entityType", "minecraft:area_effect_cloud");
		attributes().put("nbt", new NBTTagCompound(new HashMap<String, NBTTag>()));
		attributes().put("spawnOnInit", true);
	}
	
	@Override
	public List<AttributeDeclaration> getAttributeFields() {
		List<AttributeDeclaration> attributeFields = super.getAttributeFields();
		
		attributeFields.add(new AttributeDeclaration("entityType", String.class));
		attributeFields.add(new AttributeDeclaration("nbt", NBTTagCompound.class));
		attributeFields.add(new AttributeDeclaration("spawnOnInit", Boolean.class));
		
		return attributeFields;
	}
	
	/**
	 * Get entity's type.
	 * @return Entity type.
	 */
	public String getEntityType() {
		return (String) getAttribute("entityType");
	}
	
	/**
	 * Set entity's type.
	 * @param value New type.
	 */
	public void setEntityType(String value) {
		setAttribute("entityType", value);
	}
	
	/**
	 * Get the CompoundMap with this entity's NBT.
	 * @return NBT.
	 */
	public NBTTagCompound nbt() {
		return (NBTTagCompound) getAttribute("nbt");
	}
	
	/**
	 * Should this entity spawn on level init?
	 * @return Should spawn on init.
	 */
	public boolean spawnOnInit() {
		return (boolean) getAttribute("spawnOnInit");
	}
	
	/**
	 * Set whether this entity should spawn on level init.
	 * @param spawn Should spawn on init.
	 */
	public void setSpawnOnInit(boolean spawn) {
		setAttribute("spawnOnInit", spawn);
	}
	
	/**
	 * Get the nbt data of the entity in the format {data}.
	 * @return Nbt data.
	 */
	public String getNBTString() {
		return "{"+getAttribute("nbt")+"}";
	}
	
	/**
	 * Get the command used for spawning the entity
	 * @return
	 */
	public String getSpawnCommand() {
		Vector position = getPosition();
		
		// Set rotation
		List<NBTTag> rotArray = new ArrayList<NBTTag>();
		rotArray.add(new NBTTagFloat(((Number) getAttribute("rotX")).floatValue()));
		rotArray.add(new NBTTagFloat(((Number) getAttribute("rotY")).floatValue()));
		
		nbt().put("Rotation", new NBTTagList(rotArray));
		nbt().put("CustomName", new NBTTagString("\""+getName()+"\""));
		
		String command = "summon "+getEntityType()+" "+position.X()+" "+position.Y()+" "+position.Z()+" "+NBTStrings.nbtToString(nbt());
		
		nbt().getValue().remove("Rotation");
		nbt().getValue().remove("CustomName");

		return command;
	}
	
	@Override
	public JSONObject serialize() {
		JSONObject serialized = super.serialize();
		
		// Serialize nbt into string.
		serialized.getJSONObject("attributes").put("nbt", NBTStrings.nbtToString(nbt()));
		
		return serialized;
	}
	
	@Override
	public void onUnserialized(JSONObject object) {
		super.onUnserialized(object);
		
		// Parse NBT data.
		String nbt = (String) getAttribute("nbt");
		try {
			setAttribute("nbt", NBTStrings.nbtFromString(nbt));
		} catch (IOException e) {
			System.out.println("Unable to parse NBT: "+nbt);
			setAttribute("nbt", new NBTTagCompound(new HashMap<String, NBTTag>()));
		}
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
