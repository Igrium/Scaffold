package org.scaffoldeditor.scaffold.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.util.ProgressListener;

/**
 * Manages the performing and undoing of operations.
 * @author Igrium
 */
public class OperationManager {
	
	/**
	 * The operations that have been performed in order of operation.
	 * Greater index = more recent.
	 */
	public final List<Operation<?>> undoStack = new ArrayList<>();
	
	/**
	 * The operations that have been undone and are queued for redo.
	 * Greater index = more recent undo. (reverse chronology)
	 */
	public final List<Operation<?>> redoStack = new ArrayList<>();
	
	public final Level level;
	
	public OperationManager(Level level) {
		this.level = level;
	}
	
	/**
	 * Perform an operation and add it to the undo stack.
	 * If there are values in the redo stack, it gets cleared.
	 * 
	 * @param operation Operation to perform.
	 * @param listener  A progress listener that will recieve updates about this
	 *                  operation.
	 * @return A future that completes when the operation if finished and completes
	 *         exceptionally if the operation fails.
	 */
	public <T> CompletableFuture<T> execute(Operation<T> operation, ProgressListener listener) {
		CompletableFuture<T> future = new CompletableFuture<>();
		level.getProject().execute(() -> {
			try {
				future.complete(executeImpl(operation, listener));
			} catch (Throwable e) {
				future.completeExceptionally(e);
			}
		});
		return future;
	}

	/**
	 * Perform an operation and add it to the undo stack.
	 * If there are values in the redo stack, it gets cleared.
	 * 
	 * @param operation Operation to perform.
	 * @return A future that completes when the operation if finished and completes
	 *         exceptionally if the operation fails.
	 */
	public <T> CompletableFuture<T> execute(Operation<T> operation) {
		return execute(operation, ProgressListener.DUMMY);
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
	 * @throws ExecutionException   If the operation fails.
	 */
	public <T> T executeAndWait(Operation<T> operation) throws InterruptedException, ExecutionException {
		return execute(operation).get();
	}
	
	private <T> T executeImpl(Operation<T> operation, ProgressListener listener) throws Exception {
		T val = operation.execute(listener);
		// If the operation throws, execution will be stopped here.
		redoStack.clear();
		undoStack.add(operation);
		level.setHasUnsavedChanges(true);
		return val;
	}
	
	/**
	 * Undo the last operation.
	 * @return A future that completes after th undo is complete.
	 */
	public CompletableFuture<Void> undo() {
		CompletableFuture<Void> future = new CompletableFuture<>();
		level.getProject().execute(() -> {
			try {
				undoImpl();
				future.complete(null);
			} catch (Throwable e) {
				future.completeExceptionally(e);
			}
		});
		return future;
	}
	
	/**
	 * Undo the last operation and hold the current thread until it's complete.
	 * 
	 * @throws InterruptedException If the current thread was interrupted while
	 *                              waiting.
	 * @throws ExecutionException   If the undo code fails.
	 */
	public void undoAndWait() throws InterruptedException, ExecutionException {
		undo().get();
	}
	
	private void undoImpl() throws Exception {
		if (undoStack.size() > 0) {
			Operation<?> operation = undoStack.get(undoStack.size() - 1);
			operation.undo();
			undoStack.remove(undoStack.size() - 1);
			redoStack.add(operation);
			level.setHasUnsavedChanges(true);
		}
	}
	
	/**
	 * Redo the last undone operation.
	 * @return A future that completes after th undo is complete.
	 */
	public CompletableFuture<Void> redo() {
		CompletableFuture<Void> future = new CompletableFuture<>();
		level.getProject().execute(() -> {
			try {
				redoImpl();
				future.complete(null);
			} catch (Throwable e) {
				future.completeExceptionally(e);
			}
		});
		return future;
	}
	
	/**
	 * Redo the last undone operation and hold the current thread until it's
	 * complete.
	 * 
	 * @throws InterruptedException If the current thread was interrupted while
	 *                              waiting.
	 * @throws ExecutionException   If the redo code fails.
	 */
	public void redoAndWait() throws InterruptedException, ExecutionException {
		redo().get();
	}
	
	private void redoImpl() throws Exception {
		if (redoStack.size() > 0) {
			Operation<?> operation = redoStack.get(redoStack.size() - 1);
			operation.redo();
			redoStack.remove(redoStack.size() - 1);
			undoStack.add(operation);
			level.setHasUnsavedChanges(true);
		}
	}
}
