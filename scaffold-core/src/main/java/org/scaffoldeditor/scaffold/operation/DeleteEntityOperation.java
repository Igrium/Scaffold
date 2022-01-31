package org.scaffoldeditor.scaffold.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.util.ProgressListener;

public class DeleteEntityOperation implements Operation<Void> {
	
	private Level level;
	private Set<Entity> entities;
	
	private Map<Entity, StackGroup> groupCache = new HashMap<>();
	private Map<Entity, Integer> indexCache = new HashMap<>();
	
	public DeleteEntityOperation(Level level, Set<Entity> entities) {
		this.level = level;
		this.entities = Set.copyOf(entities);
	}

	@Override
	public Void execute(ProgressListener listener) {
		for (Entity ent : entities) {
			StackGroup owner = level.getLevelStack().getOwningGroup(ent);
			if (owner == null) {
				throw new IllegalStateException("Unable to delete entity: "+ent+" because it is not in the level!");
			}
			groupCache.put(ent, owner);
			indexCache.put(ent, owner.indexOf(ent));
			level.removeEntity(ent);
		}
		return null;
	}

	@Override
	public void undo() {
		for (Entity ent : entities) {
			level.addEntity(ent, groupCache.get(ent), indexCache.get(ent));
		}
	}

	@Override
	public void redo() {
		for (Entity ent : entities) {
			level.removeEntity(ent);
		}
	}

	@Override
	public String getName() {
		return "Delete Entity";
	}
}
