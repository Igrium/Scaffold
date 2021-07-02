package org.scaffoldeditor.scaffold.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;

public class DeleteEntityOperation implements Operation {
	
	private Level level;
	private Set<Entity> entities;
	private boolean recompile = false;
	
	private Map<Entity, StackGroup> groupCache = new HashMap<>();
	private Map<Entity, Integer> indexCache = new HashMap<>();
	
	public DeleteEntityOperation(Level level, Set<Entity> entities) {
		this.level = level;
		this.entities = Set.copyOf(entities);
	}

	@Override
	public boolean execute() {
		for (Entity ent : entities) {
			StackGroup owner = level.getLevelStack().getOwningGroup(ent);
			if (owner == null) {
				LogManager.getLogger().error("Unable to delete entity: "+ent+" because it is not in the level!");
				return false;
			}
			groupCache.put(ent, owner);
			indexCache.put(ent, owner.indexOf(ent));
			level.removeEntity(ent, true);
			if (ent instanceof BlockEntity) recompile = true;
		}
		if (recompile && level.autoRecompile) {
			level.quickRecompile();
		}
		return true;
	}

	@Override
	public void undo() {
		for (Entity ent : entities) {
			level.addEntity(ent, groupCache.get(ent), indexCache.get(ent), true);
		}
		if (recompile && level.autoRecompile) {
			level.quickRecompile();
		}
	}

	@Override
	public void redo() {
		for (Entity ent : entities) {
			level.removeEntity(ent);
		}
		if (recompile && level.autoRecompile) {
			level.quickRecompile();
		}
	}

	@Override
	public String getName() {
		return null;
	}
}
