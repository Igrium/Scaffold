package org.scaffoldeditor.scaffold.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.level.stack.StackItem;

public class DeleteGroupOperation implements Operation {
	
	Level level;
	Set<StackGroup> groups;
	
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
			
			if (level.removeGroup(group)) success = true;
		}
		level.updateLevelStack();
		return success;
	}

	@Override
	public void undo() {
		for (StackGroup group : groups) {
			level.addGroup(group, groupCache.get(group), indexCache.get(group));
		}
		level.updateLevelStack();
	}

	@Override
	public void redo() {
		for (StackGroup group : groups) {
			level.removeGroup(group);
		}
		level.updateLevelStack();
	}

	@Override
	public String getName() {
		return "Delete group";
	}

}
