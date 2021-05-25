package org.scaffoldeditor.scaffold.operation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;

public class DeleteEntityOperation implements Operation {
	
	private Level level;
	private Set<Entity> entities;
	private Map<String, Integer> stackCache = new HashMap<>();
	
	public DeleteEntityOperation(Level level, Set<Entity> entities) {
		this.level = level;
		this.entities = new HashSet<>(entities);
	}

	@Override
	public boolean execute() {
		for (Entity ent : entities) {
			onUpdateEnt(ent);
			
			stackCache.put(ent.getName(), level.getEntityStack().indexOf(ent.getName()));
			level.getEntities().remove(ent.getName());
			level.getEntityStack().remove(ent.getName());
		}
		level.updateEntityStack();
		if (level.autoRecompile) {
			level.quickRecompile();
		}
		return true;
	}

	@Override
	public void undo() {
		for (Entity ent : entities) {
			level.getEntities().put(ent.getName(), ent);
			level.getEntityStack().add(stackCache.get(ent.getName()), ent.getName());
			onUpdateEnt(ent);
		}
		level.updateEntityStack();
		if (level.autoRecompile) {
			level.quickRecompile();
		}
	}

	@Override
	public void redo() {
		for (Entity ent : entities) {
			onUpdateEnt(ent);
			level.getEntities().remove(ent.getName());
			level.getEntityStack().remove(ent.getName());
		}
		level.updateEntityStack();
		if (level.autoRecompile) {
			level.quickRecompile();
		}
	}

	@Override
	public String getName() {
		return null;
	}
	
	private void onUpdateEnt(Entity ent) {
		if (ent instanceof BlockEntity) {
			level.dirtySections.addAll(((BlockEntity) ent).getOverlappingSections(level.getBlockWorld()));
		}
	}
}
