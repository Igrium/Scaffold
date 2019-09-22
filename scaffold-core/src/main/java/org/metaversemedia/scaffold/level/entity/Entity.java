package org.metaversemedia.scaffold.level.entity;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.metaversemedia.scaffold.level.Attribute;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.math.Vector;

/**
 * Base entity class in maps
 * @author Sam54123
 *
 */
public class Entity {
	/* Position of the entity in world space */
	private Vector position;
	
	/* Name of the entity */
	private String name;
	
	/* The level this entity belongs to */
	private Level level;
	
	private Map<String, Attribute> attributes = new HashMap<String, Attribute>();

	
	/**
	 * Construct a new entity with a name and a level.
	 * @param level	Level entity should belong to.
	 * @param name Entity name
	 */
	public Entity(Level level, String name) {
		this.name = name;
		this.level = level;
	}
	
	/**
	 * Get this entity's name.
	 * @return Name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set this entity's name. (DON'T CALL MANUALLY!)
	 * @param name New name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get this entity's world position.
	 * @return Position
	 */
	public Vector getPosition() {
		return position;
	}
	
	/**
	 * Set this entity's world position.
	 * Called on entity creation.
	 * @param position New position
	 */
	public void setPosition(Vector position) {
		this.position = position;
	}
	
	/**
	 * Get the level this entity belongs to.
	 * @return Level
	 */
	public Level getLevel() {
		return level;
	}
	
	/**
	 * Get a map of this entity's attributes.
	 * @return Attributes
	 */
	public Map<String, Attribute> attributes() {
		return attributes;
	}
	
	/**
	 * Create a new attribute and add it.
	 * @param name Attribute name.
	 * @param type Attribute type.
	 */
	public void addAttribute(String name, Attribute.Type type) {
		attributes.put(name, new Attribute(type));
	}
	
	/**
	 * Create a new attribute and add it.
	 * @param name Attribute name.
	 * @param value Attribute value.
	 */
	public void addAttribute(String name, Object value) {
		attributes.put(name, new Attribute(value));
	}
	
	
	/**
	 * Serialize this entity into a JSON object.
	 * @return Serialized entity
	 */
	public JSONObject serialize() {
		// Create object
		JSONObject object = new JSONObject();
		
		// Basic information
		object.put("type", getClass().getName());
		object.put("position", position.toJSONArray());
		
		// Attributes
		JSONObject attributeObject = new JSONObject();
		
		for (String key : attributes.keySet()) {
			if (attributes.get(key).getValue() != null) {
				attributeObject.put(key, attributes.get(key).serialize());
			}
		}
		
		object.put("attributes", attributeObject);
		
		return object;
	}

	
	/**
	 * Unserialize an entity fom a JSON object.
	 * @param level Level this entity should belong to
	 * @param name Name of the entity
	 * @param object JSON object to unserialize from
	 * @return Unserialized entity
	 */
	public static Entity unserialize(Level level, String name, JSONObject object) {
		try {
			// Create object
			Entity entity = new Entity(level, name);
			
			// Basic info
			entity.setPosition(Vector.fromJSONArray(object.getJSONArray("position")));
			
			// Attributes
			JSONObject attributes = object.getJSONObject("attributes");
			
			for (String key : attributes.keySet()) {
				Attribute attribute = Attribute.unserialize(attributes.getJSONObject(key));
				entity.attributes().put(key, attribute);
			}
			
			return entity;
		} catch (JSONException e) {
			System.out.println("Improperly formatted entity: "+name);
			return null;
		}
		
	}
	
	/**
	 * Compile this entity's logic.
	 * @param logicFolder Folder comtaining level's function files.
	 * @return Success
	 */
	public boolean compileLogic(Path logicFolder) {
		return true;
	}
}
