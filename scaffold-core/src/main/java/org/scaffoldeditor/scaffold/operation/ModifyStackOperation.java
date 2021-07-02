package org.scaffoldeditor.scaffold.operation;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.util.LevelOperations;

/**
 * Updates the entity stack.
 * @author Igrium
 */
public class ModifyStackOperation implements Operation {
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
	public boolean execute() {
		oldStack = level.getLevelStack().copy();
		LevelOperations.modifyLevelStack(level, newStack, false);
		
		if (level.autoRecompile) {
			level.quickRecompile();
		}
		
		return true;
	}
	@Override
	public void undo() {
		LevelOperations.modifyLevelStack(level, oldStack, false);
		
		if (level.autoRecompile) {
			level.quickRecompile();
		}
	}
	@Override
	public void redo() {
		LevelOperations.modifyLevelStack(level, newStack, false);
		
		if (level.autoRecompile) {
			level.quickRecompile();
		}
	}
	@Override
	public String getName() {
		return "Update level stack";
	}
	
}
