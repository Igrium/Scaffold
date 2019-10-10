package org.metaversemedia.scaffold.level.entity;

import java.util.List;

import org.json.JSONObject;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.Entity.AttributeDeclaration;
import org.metaversemedia.scaffold.logic.Datapack;
import org.metaversemedia.scaffold.math.Vector;

import com.flowpowered.nbt.CompoundMap;

/**
 * Represents a Minecraft entity in the editor
 * @author Sam54123
 *
 */
public class GameEntity extends Rotatable {

	public GameEntity(Level level, String name) {
		super(level, name);
		attributes().put("entityType", "minecraft:area_effect_cloud");
		attributes().put("nbt", "");
		attributes().put("spawnOnInit", true);
	}
	
	@Override
	public List<AttributeDeclaration> getAttributeFields() {
		List<AttributeDeclaration> attributeFields = super.getAttributeFields();
		
		attributeFields.add(new AttributeDeclaration("entityType", String.class));
		attributeFields.add(new AttributeDeclaration("nbt", String.class));
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
		return "summon "+getEntityType()+" "+position.X()+" "+position.Y()+" "+position.Z()+" "+getNBTString();
		
	}
	
	
	@Override
	public void onUnserialized(JSONObject object) {
		super.onUnserialized(object);
	}
	
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		super.compileLogic(datapack);
		
		if (spawnOnInit()) {
			getLevel().initFunction().commands().add(getSpawnCommand());
		}
		
		return true;
	}
}
