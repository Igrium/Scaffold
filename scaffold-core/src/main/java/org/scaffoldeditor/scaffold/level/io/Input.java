package org.scaffoldeditor.scaffold.level.io;

import org.scaffoldeditor.scaffold.level.entity.Entity;

/**
 * Represents an input in the entity IO system.
 * @author Sam54123
 */
public abstract class Input {
	
	private Entity parent;
	
	/**
	 * Create a new input.
	 * @param parent Parent entity.
	 */
	public Input(Entity parent) {
		this.parent = parent;
	}
	
	/**
	 * Get this input's parent entity.
	 * @return Parent.
	 */
	public Entity getParent() {
		return parent;
	}
	
	/**
	 * Get this input's name.
	 * @return Name.
	 */
	public abstract String getName();
	
	/**
	 * Does this input take arguements?
	 * @return Takes arguements?
	 */
	public abstract boolean takesArgs();
	
	/**
	 * Generate the command to run when triggered.
	 * @param instigator Entity that started the io chain.
	 * @param caller Entity that is calling this input.
	 * @param args Input arguements.
	 * @return Generated command.
	 */
	public abstract String getCommand(Entity instigator, Entity caller, String[] args);
}
