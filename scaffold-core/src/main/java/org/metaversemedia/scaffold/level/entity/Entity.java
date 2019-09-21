package org.metaversemedia.scaffold.level.entity;

import org.json.JSONObject;
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
	
	/**
	 * Construct a new entity with a name and a level.
	 * @param level	Level entity should belong to
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
	 * Serialize this entity into a JSON object.
	 * @return Serialized entity
	 */
	public JSONObject serialize() {
		JSONObject object = new JSONObject();
		object.put("type", getClass().getName());
		object.put("position", position.toJSONArray());
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
		Entity entity = new Entity(level, name);
		entity.setPosition(Vector.fromJSONArray(object.getJSONArray("position")));
		return entity;
	}
	
	
}
