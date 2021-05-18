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
	 * @param registryName Registry name of the entity to spawn.
	 * @param level Level to spawn in.
	 * @param name Name to assign.
	 * @return Newly created entity.
	 */
	public static Entity createEntity(String registryName, Level level, String name) {
		if (!registry.containsKey(registryName)) {
			System.err.println("Unknown entity type: "+registryName);
			return null;
		}
		
		Entity entity = registry.get(registryName).create(level, name);
		entity.registryName = registryName;
		return entity;
	}
}
