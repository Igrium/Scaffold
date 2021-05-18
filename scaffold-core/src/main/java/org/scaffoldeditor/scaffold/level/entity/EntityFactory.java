package org.scaffoldeditor.scaffold.level.entity;

import org.scaffoldeditor.scaffold.level.Level;

public interface EntityFactory<T extends Entity> {
	
	/**
	 * Create an instance of the entity.
	 * @param level Level to spawn into
	 * @param name Name of the entity.
	 * @return Newly created entity.
	 */
	public T create(Level level, String name);
}
