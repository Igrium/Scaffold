package org.metaversemedia.scaffold.level;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.math.Vector;

/**
 * Represents a single level file
 * @author Sam54123
 *
 */
public class Level {
	
	/* The project this level belongs to */
	private Project project;
	
	/* Relative path to level file */
	private Path path;
	
	/* Has this level been saved? */
	private boolean isSaved = false;
	
	/* All the entities in the map */
	private Map<String, Entity> entities = new HashMap();
	
	/**
	 * Create a new level
	 * @param project Project to create level in
	 */
	public Level(Project project) {
		this.project = project;
	}
	
	/**
	 * Get the Project the level is a part of
	 * @return Assigned project
	 */
	public Project getProject() {
		return project;
	}
	
	/**
	 * Has this level been saved?
	 * @return Is saved?
	 */
	public boolean isSaved() {
		return isSaved;
	}
	
	/**
	 * Mark the level as unsaved
	 */
	public void markUnsaved() {
		isSaved = false;
	}
	
	/**
	 * Create a new entity.
	 * @param entityType Class of entity object to create
	 * @param name Name of entity
	 * @param position Position of entity
	 * @return Newly created entity
	 */
	public Entity newEntity(Class<Entity> entityType, String name, Vector position) {
		// Make sure entity with name doesn't already exist
		while (entities.get(name) != null) {
			// Attempt to increment number
			if (Character.isDigit(name.charAt(name.length()-1))) {
				int lastNum = Character.getNumericValue(name.charAt(name.length()-1))+1;
				name = name.substring(0,name.length() - 1) + lastNum;
			} else {
				name = name+'1';
			}

		}
		// Create entity
		Entity entity = null;
		try {
			entity = entityType.getDeclaredConstructor(new Class[] {Level.class,String.class}).newInstance(this, name);
			entity.setPosition(position);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
		
		// Add to entities list
		entities.put(name, entity);
		
		return entity;
	}
	
}
