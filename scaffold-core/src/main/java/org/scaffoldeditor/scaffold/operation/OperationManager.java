package org.scaffoldeditor.scaffold.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
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
		level.getProject().execute(() -> {
			future.complete(executeImpl(operation));
		});
		return future;
	}

	/**
	 * Perform an operation and add it to the undo stack. Holds the current thread
	 * untill the operation is complete. If there are values in the redo stack, it
	 * gets cleared.
	 * 
	 * @param operation Operation to perform.
	 * @return Operation success.
	 * @throws InterruptedException If the current thread was interrupted while
	 *                              waiting.
	 */
	public boolean executeAndWait(Operation operation) throws InterruptedException {
		try {
			return execute(operation).get();
		} catch (ExecutionException e) {
			LogManager.getLogger().error(e);
			return false;
		}
	}
	
	private boolean executeImpl(Operation operation) {
		if (operation.execute()) {
			redoStack.clear();
			undoStack.add(operation);
			level.setHasUnsavedChanges(true);
			return true;
		}
		return false;
	}
	
	/**
	 * Undo the last operation.
	 */
	public void undo() {
		level.getProject().execute(() -> {
			undoImpl();
		});
	}
	
	/**
	 * Undo the last operation and hold the current thread until it's complete.
	 * 
	 * @throws InterruptedException If the current thread was interrupted while
	 *                              waiting.
	 */
	public void undoAndWait() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		level.getProject().execute(() -> {
			undoImpl();
			latch.countDown();
		});
		
		latch.await();
	}
	
	private void undoImpl() {
		if (undoStack.size() > 0) {
			Operation operation = undoStack.get(undoStack.size() - 1);
			operation.undo();
			undoStack.remove(undoStack.size() - 1);
			redoStack.add(operation);
			level.setHasUnsavedChanges(true);
		}
	}
	
	/**
	 * Redo the last undone operation.
	 */
	public void redo() {
		level.getProject().execute(() -> {
			redoImpl();
		});
	}
	
	/**
	 * Redo the last undone operation and hold the current thread until it's
	 * complete.
	 * 
	 * @throws InterruptedException If the current thread was interrupted while
	 *                              waiting.
	 */
	public void redoAndWait() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		level.getProject().execute(() -> {
			redoImpl();
			latch.countDown();
		});
		latch.await();
	}
	
	private void redoImpl() {
		if (redoStack.size() > 0) {
			Operation operation = redoStack.get(redoStack.size() - 1);
			operation.redo();
			redoStack.remove(redoStack.size() - 1);
			undoStack.add(operation);
			level.setHasUnsavedChanges(true);
		}
	}
}
