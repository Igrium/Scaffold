package org.scaffoldeditor.scaffold.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.level.Level;

public final class EntityRegistry {
	
	/**
	 * The registry of entity types.
	 * <br>
	 * (type name, entity factory)
	 */
	public static final Map<String, EntityFactory<?>> registry = new HashMap<>();
	
	/**
	 * A registry of entitity classes. Auto-generated from the primary registry.
	 */
	private static final Map<String, Class<? extends Entity>> classRegistry = new HashMap<>();
	
	/**
	 * Spawn an entity.
	 * @param registryName Registry name of the entity to spawn.
	 * @param level Level to spawn in.
	 * @param name Name to assign.
	 * @return Newly created entity.
	 */
	public static Entity createEntity(String registryName, Level level, String name) {
		return createEntity(registryName, level, name, false);
	}
	
	/**
	 * Spawn an entity.
	 * @param registryName Registry name of the entity to spawn.
	 * @param level Level to spawn in.
	 * @param name Name to assign.
	 * @param supressUpdate Don't call <code>onUpdateAttributes()</code> when spawning entity.
	 * @return Newly created entity.
	 */
	public static Entity createEntity(String registryName, Level level, String name, boolean supressUpdate) {
		if (!registry.containsKey(registryName)) {
			LogManager.getLogger().error("Unknown entity type: "+registryName);
			return null;
		}
		
		Entity entity = registry.get(registryName).create(level, name);
		entity.registryName = registryName;
		return entity;
	}
	
	/**
	 * Get the class of an entity type.
	 * @param registryName Registry name of the entity type.
	 * @return Entity class, or <code>null</code> if no entity of that type exists.
	 */
	public static Class<? extends Entity> getClass(String registryName) {
		Class<? extends Entity> val = classRegistry.get(registryName);
		if (val != null) return val;
		
		if (!registry.containsKey(registryName)) {
			LogManager.getLogger().error("Unknown entity type: "+registryName);
			return null;
		}
		
		Entity entity = registry.get(registryName).create(null, "temp");
		val = entity.getClass();
		classRegistry.put(registryName, val);
		return val;
	}
}
