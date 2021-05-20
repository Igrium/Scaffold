package org.scaffoldeditor.scaffold.operation;

import java.util.ArrayList;
import java.util.List;

import org.scaffoldeditor.scaffold.level.Level;

/**
 * Manages the performing and undoing of operations.
 * @author Igrium
 */
public class OperationManager {
	
	/**
	 * The operations that have been performed in order of operation.
	 * Greater index = more recent.
	 */
	public final List<Operation> undoStack = new ArrayList<>();
	
	/**
	 * The operations that have been undone and are queued for redo.
	 * Greater index = more recent undo. (reverse chronology)
	 */
	public final List<Operation> redoStack = new ArrayList<>();
	
	public final Level level;
	
	public OperationManager(Level level) {
		this.level = level;
	}
	
	/**
	 * Perform an operation and add it to the undo stack.
	 * If there are values in the redo stack, it gets cleared.
	 * @param operation Operation to perform.
	 * @return Operation success.
	 */
	public boolean execute(Operation operation) {
		if (operation.execute()) {
			redoStack.clear();
			undoStack.add(operation);
			return true;
		}
		return false;
	}
	
	public void undo() {
		if (undoStack.size() > 0) {
			Operation operation = undoStack.get(undoStack.size() - 1);
			operation.undo();
			undoStack.remove(undoStack.size() - 1);
			redoStack.add(operation);
		}
	}
	
	public void redo() {
		if (redoStack.size() > 0) {
			Operation operation = redoStack.get(redoStack.size() - 1);
			operation.redo();
			redoStack.remove(redoStack.size() - 1);
			undoStack.add(operation);
		}
	}
}
