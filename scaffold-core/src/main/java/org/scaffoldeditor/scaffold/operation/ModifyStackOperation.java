package org.scaffoldeditor.scaffold.operation;

import java.util.ArrayList;
import java.util.List;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.util.LevelOperations;

/**
 * Updates the entity stack.
 * @author Igrium
 */
public class ModifyStackOperation implements Operation {
	private Level level;
	private List<String> newStack;
	private List<String> newStackCloned = new ArrayList<>();
	private List<String> oldStack = new ArrayList<>();
	
	/**
	 * Create a modify stack operator.
	 * @param level Level to target.
	 * @param newStack Updated stack. Doesn't get commited untill execution.
	 * Before that, changes made to the list will be reflected.
	 */
	public ModifyStackOperation(Level level, List<String> newStack) {
		this.level = level;
		this.newStack = newStack;
	}
	
	@Override
	public boolean execute() {
		newStackCloned.addAll(newStack);
		oldStack.addAll(level.getEntityStack());
		LevelOperations.modifyEntityStack(level, newStack, false);
		
		if (level.autoRecompile) {
			level.quickRecompile();
		}
		
		return true;
	}
	@Override
	public void undo() {
		LevelOperations.modifyEntityStack(level, oldStack, false);
		
		if (level.autoRecompile) {
			level.quickRecompile();
		}
	}
	@Override
	public void redo() {
		LevelOperations.modifyEntityStack(level, newStack, false);
		
		if (level.autoRecompile) {
			level.quickRecompile();
		}
	}
	@Override
	public String getName() {
		return "Update entity stack";
	}
	
}
