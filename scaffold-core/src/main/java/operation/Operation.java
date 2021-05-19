package operation;

/**
 * An operation is an undoable action performed by the user.
 * Each time an operator is performed, a new instance of
 * the operator is created and added to the undo stack.
 * @author Igrium
 */
public interface Operation {
	
	/**
	 * The different states an operation can be in.
	 * This doesn't do anything by default; it's provided only for convenience.
	 * @author Igrium
	 */
	public enum State { NEW, FINISHED, UNDONE }
	
	/**
	 * Execute the operation.
	 * @return Was the operation successful?
	 */
	public boolean execute();
	
	/**
	 * Undo the operation.
	 * Assumes level state is exactly as the operator left it.
	 */
	public void undo();
	
	/**
	 * Redo the operation.
	 * Assumes level state is exactly as it was when operator was perform
	 */
	public void redo();
	
	/**
	 * Get the operation's name (for rendering in the ui).
	 * @return Operation name.
	 */
	public String getName();
}
