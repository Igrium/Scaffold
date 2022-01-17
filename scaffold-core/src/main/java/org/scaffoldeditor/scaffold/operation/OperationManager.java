package org.scaffoldeditor.scaffold.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.scaffoldeditor.scaffold.core.ServiceProvider;
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
	private ServiceProvider serviceProvider;
	
	public OperationManager(Level level) {
		this.level = level;
	}
	
	/**
	 * Get the current service provider.
	 * @return The service provider, or <code>null</code> if there is none.
	 */
	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	/**
	 * Set a service provider to execute all operations.
	 * @param serviceProvider Service provider to use.
	 */
	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	
	/**
	 * <p>
	 * Perform an operation and add it to the undo stack, clearing the redo stack in
	 * the process
	 * </p>
	 * <p>
	 * If there is a registered service provider, the operation is run with said
	 * provider. If not, it is run on the current thread.
	 * </p>
	 * 
	 * @param operation Operation to perform.
	 * @param listener  A progress listener that will recieve updates about this
	 *                  operation.
	 * @return A future that completes when the operation if finished and completes
	 *         exceptionally if the operation fails.
	 */
	public <T> CompletableFuture<T> execute(Operation<T> operation, ProgressListener listener) {
		CompletableFuture<T> future = new CompletableFuture<>();
		Runnable exec = () -> {
			try {
				future.complete(executeImpl(operation, listener));
			} catch (Throwable e) {
				future.completeExceptionally(e);
			}
		};

		if (serviceProvider != null) {
			serviceProvider.execute(exec);
		} else {
			exec.run();
		}

		return future;
	}

	/**
	 * <p>
	 * Perform an operation and add it to the undo stack, clearing the redo stack in
	 * the process
	 * </p>
	 * <p>
	 * If there is a registered service provider, the operation is run with said
	 * provider. If not, it is run on the current thread.
	 * </p>
	 * 
	 * @param operation Operation to perform.
	 * @return A future that completes when the operation if finished and completes
	 *         exceptionally if the operation fails.
	 */
	public <T> CompletableFuture<T> execute(Operation<T> operation) {
		return execute(operation, ProgressListener.DUMMY);
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
	 * <p>
	 * Undo the last operation.
	 * </p>
	 * <p>
	 * If there is a registered service provider, the undo operation is run with
	 * said provider. If not, it is run on the current thread.
	 * </p>
	 * 
	 * @return A future that completes after th undo is complete.
	 */
	public CompletableFuture<Void> undo() {
		CompletableFuture<Void> future = new CompletableFuture<>();
		Runnable exec = () -> {
			try {
				undoImpl();
				future.complete(null);
			} catch (Throwable e) {
				future.completeExceptionally(e);
			}
		};
		if (serviceProvider != null) {
			serviceProvider.execute(exec);
		} else {
			exec.run();
		}
		return future;
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
	 * <p>
	 * Redo the last operation.
	 * </p>
	 * <p>
	 * If there is a registered service provider, the redo operation is run with
	 * said provider. If not, it is run on the current thread.
	 * </p>
	 * 
	 * @return A future that completes after th redo is complete.
	 */
	public CompletableFuture<Void> redo() {
		CompletableFuture<Void> future = new CompletableFuture<>();
		Runnable exec = () -> {
			try {
				redoImpl();
				future.complete(null);
			} catch (Throwable e) {
				future.completeExceptionally(e);
			}
		};
		if (serviceProvider != null) {
			serviceProvider.execute(exec);
		} else {
			exec.run();
		}
		return future;
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
