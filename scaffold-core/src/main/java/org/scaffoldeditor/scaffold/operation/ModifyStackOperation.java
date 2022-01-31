package org.scaffoldeditor.scaffold.operation;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.util.LevelOperations;
import org.scaffoldeditor.scaffold.util.ProgressListener;

/**
 * Updates the entity stack.
 * @author Igrium
 */
public class ModifyStackOperation implements Operation<StackGroup> {
	private Level level;
	private StackGroup newStack;
	private StackGroup oldStack;
	
	/**
	 * Create a modify stack operation.
	 * @param level Level to target.
	 * @param newStack Updated stack.
	 */
	public ModifyStackOperation(Level level, StackGroup newStack) {
		this.newStack = newStack.copy();
		this.level = level;
	}
	
	@Override
	public StackGroup execute(ProgressListener listener) {
		oldStack = level.getLevelStack().copy();
		LevelOperations.modifyLevelStack(level, newStack, false);
		
		return newStack;
	}
	
	@Override
	public void undo() {
		LevelOperations.modifyLevelStack(level, oldStack, false);
	}

	@Override
	public void redo() {
		LevelOperations.modifyLevelStack(level, newStack, false);
	}

	@Override
	public String getName() {
		return "Update level stack";
	}
	
}
