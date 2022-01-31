package org.scaffoldeditor.scaffold.operation;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.level.stack.StackItem;
import org.scaffoldeditor.scaffold.util.ProgressListener;

public class AddGroupOperation implements Operation<StackItem> {
	private StackGroup parent;
	private String name;
	private Level level;
	
	private StackItem added;
	
	public AddGroupOperation(StackGroup parent, String name, Level level) {
		this.parent = parent;
		this.name = name;
		this.level = level;
	}

	@Override
	public StackItem execute(ProgressListener listener) {
		added = new StackItem(new StackGroup(name));
		parent.items.add(added);
		level.updateLevelStack();
		
		return added;
	}

	@Override
	public void undo() {
		parent.items.remove(added);
		level.updateLevelStack();
	}

	@Override
	public void redo() {
		parent.items.add(added);
		level.updateLevelStack();
	}

	@Override
	public String getName() {
		return "Create entity group";
	}
}
