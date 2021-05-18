package org.scaffoldeditor.scaffold.level.entity;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.scaffold.level.Level;

public final class EntityRegistry {
	
	/**
	 * The registry of entity types.
	 * <br>
	 * (type name, entity factory)
	 */
	public static final Map<String, EntityFactory<Entity>> registry = new HashMap<>();
	
	/**
	 * Spawn an entity.
	 * @param typeName Type of entity to spawn.
	 * @param level Level to spawn in.
	 * @param name Name to assign.
	 * @return Newly created entity.
	 */
	public static Entity createEntity(String typeName, Level level, String name) {
		if (!registry.containsKey(typeName)) {
			System.err.println("Unknown entity type: "+typeName);
			return null;
		}
		
		Entity entity = registry.get(typeName).create(level, name);
		entity.typeName = typeName;
		return entity;
	}
}
