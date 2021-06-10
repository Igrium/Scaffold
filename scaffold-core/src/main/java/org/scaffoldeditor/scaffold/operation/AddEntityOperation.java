package org.scaffoldeditor.scaffold.operation;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.math.Vector;

public class AddEntityOperation implements Operation {

	private String registryName;
	private Level level;
	private String name;
	private Vector position;
	private Entity entity;
	
	public AddEntityOperation(Level level, String registryName, String name, Vector position) {
		this.registryName = registryName;
		this.level = level;
		this.name = name;
		this.position = position;
	}
	
	@Override
	public boolean execute() {
		try {
			entity = level.newEntity(registryName, name, position);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public void undo() {
		level.removeEntity(entity.getName());
	}

	@Override
	public void redo() {
		level.addEntity(entity);
	}

	@Override
	public String getName() {
		return "Add Entity";
	}
	
	/**
	 * Get the entity that was spawned.
	 * @return The entity that was spawned, or null if the operation hasn't run yet.
	 */
	public Entity getEntity() {
		return entity;
	}

}
