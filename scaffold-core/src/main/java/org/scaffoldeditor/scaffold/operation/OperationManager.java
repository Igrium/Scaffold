package org.scaffoldeditor.scaffold.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
	public CompletableFuture<Boolean> execute(Operation operation) {
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		level.getProject().getLevelService().execute(() -> {
			if (operation.execute()) {
				redoStack.clear();
				undoStack.add(operation);
				level.setHasUnsavedChanges(true);
				future.complete(true);
				return;
			}
			level.setHasUnsavedChanges(true);
			future.complete(false);
		});
		return future;
	}
	
	public void undo() {
		level.getProject().getLevelService().execute(() -> {
			if (undoStack.size() > 0) {
				Operation operation = undoStack.get(undoStack.size() - 1);
				operation.undo();
				undoStack.remove(undoStack.size() - 1);
				redoStack.add(operation);
				level.setHasUnsavedChanges(true);
			}
		});
	}
	
	public void redo() {
		level.getProject().getLevelService().execute(() -> {
			if (redoStack.size() > 0) {
				Operation operation = redoStack.get(redoStack.size() - 1);
				operation.redo();
				redoStack.remove(redoStack.size() - 1);
				undoStack.add(operation);
				level.setHasUnsavedChanges(true);
			}
		});
	}
}
