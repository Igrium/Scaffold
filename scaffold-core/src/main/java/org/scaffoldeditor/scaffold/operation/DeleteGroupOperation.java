package org.scaffoldeditor.scaffold.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.level.stack.StackItem;

public class DeleteGroupOperation implements Operation {
	
	Level level;
	Set<StackGroup> groups;
	private boolean recompile = false;
	
	private Map<StackGroup, StackGroup> groupCache = new HashMap<>();
	private Map<StackGroup, Integer> indexCache = new HashMap<>();
	private boolean success = false;
	
	public DeleteGroupOperation(Level level, Set<StackGroup> groups) {
		this.level = level;
		this.groups = groups;
	}

	@Override
	public boolean execute() {
		for (StackGroup group : groups) {
			StackGroup owner = level.getLevelStack().getOwningGroup(new StackItem(group));
			if (owner == null) {
				LogManager.getLogger().error("Unable to delete group: "+group+" because it is not in the level!");
				return false;
			}
			
			groupCache.put(group, owner);
			indexCache.put(group, owner.items.indexOf(new StackItem(group)));
			
			for (Entity entity : group) {
				if (entity instanceof BlockEntity) recompile = true;
			}
			
			if (level.removeGroup(group, true)) success = true;
		}
		level.updateLevelStack();
		
		if (success && recompile && level.autoRecompile) {
			level.quickRecompile();
		}
		return success;
	}

	@Override
	public void undo() {
		for (StackGroup group : groups) {
			level.addGroup(group, groupCache.get(group), indexCache.get(group), true);
		}
		level.updateLevelStack();
		if (recompile && level.autoRecompile) {
			level.quickRecompile();
		}
	}

	@Override
	public void redo() {
		for (StackGroup group : groups) {
			level.removeGroup(group, true);
		}
		level.updateLevelStack();
		if (recompile && level.autoRecompile) {
			level.quickRecompile();
		}
	}

	@Override
	public String getName() {
		return "Delete group";
	}

}
