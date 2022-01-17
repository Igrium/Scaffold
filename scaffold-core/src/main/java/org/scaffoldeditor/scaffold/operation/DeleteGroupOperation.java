package org.scaffoldeditor.scaffold.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.level.stack.StackItem;
import org.scaffoldeditor.scaffold.util.ProgressListener;

public class DeleteGroupOperation implements Operation<Void> {
	
	Level level;
	Set<StackGroup> groups;
	
	private Map<StackGroup, StackGroup> groupCache = new HashMap<>();
	private Map<StackGroup, Integer> indexCache = new HashMap<>();
	
	public DeleteGroupOperation(Level level, Set<StackGroup> groups) {
		this.level = level;
		this.groups = groups;
	}

	@Override
	public Void execute(ProgressListener listener) {
		for (StackGroup group : groups) {
			StackGroup owner = level.getLevelStack().getOwningGroup(new StackItem(group));
			if (owner == null) {
				throw new IllegalStateException("Unable to delete group: "+group+" because it is not in the level!");
			}
			
			groupCache.put(group, owner);
			indexCache.put(group, owner.items.indexOf(new StackItem(group)));
		}
		level.updateLevelStack();
		return null;
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
