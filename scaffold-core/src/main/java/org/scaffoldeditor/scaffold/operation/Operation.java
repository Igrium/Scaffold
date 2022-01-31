package org.scaffoldeditor.scaffold.operation;

import org.scaffoldeditor.scaffold.util.ProgressListener;

/**
 * An operation is an undoable action performed by the user.
 * Each time an operator is performed, a new instance of
 * the operator is created and added to the undo stack.
 * @author Igrium
 */
public interface Operation<T> {
	
	/**
	 * The different states an operation can be in.
	 * This doesn't do anything by default; it's provided only for convenience.
	 * @author Igrium
	 */
	public enum State { NEW, FINISHED, UNDONE }
	
	/**
	 * Execute the operation.
	 * @param progress A progress listener that will accept updates about this operation.
	 * @return Operation return value
	 * @throws Exception Any exception that might have been thrown by the operation code.
	 */
	public T execute(ProgressListener progress) throws Exception;

	/**
	 * Execute the operation.
	 * @return Operation return value
	 * @throws Exception Any exception that might have been thrown by the operation code.
	 */
	default T execute() throws Exception {
		return(execute(ProgressListener.DUMMY));
	}
	
	/**
	 * Undo the operation.
	 * Assumes level state is exactly as the operator left it.
	 * @throws Exception Any exception that might have occured while undoing.
	 */
	public void undo() throws Exception;
	
	/**
	 * Redo the operation.
	 * Assumes level state is exactly as it was when operator was perform
	 * @throws Exception Any exception that might have occured while redoing.
	 */
	public void redo() throws Exception;
	
	/**
	 * Get the operation's name (for rendering in the ui).
	 * @return Operation name.
	 */
	public String getName();
}
