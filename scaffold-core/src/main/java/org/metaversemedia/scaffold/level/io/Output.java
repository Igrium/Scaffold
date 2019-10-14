package org.metaversemedia.scaffold.level.io;

import org.metaversemedia.scaffold.level.entity.Entity;

/**
 * Represents an entity output connection
 * 
 * @author Sam54123
 */
public class Output {
	private Entity parent;

	/**
	 * Create a new output connection.
	 * 
	 * @param parent Parent entity.
	 */
	public Output(Entity parent) {

	}

	/**
	 * The name of the output to trigger on.
	 */
	public String name = "";

	/**
	 * The input to trigger when called.
	 */
	public Input connection = null;

	/**
	 * Arguements to call the input with.
	 */
	public String[] args;
	
	/**
	 * Check if the output is valid to call.
	 * @return Validity.
	 */
	public boolean isValid() {
		return (!name.matches("") && connection != null);
	}
	
	/**
	 * Compile this output into a Minecraft command.
	 * @param instigator Instigator entity.
	 * @return Compiled command.
	 */
	public String compile(Entity instigator) {
		if (connection != null) {
			return connection.getCommand(instigator, parent, args);
		} else {
			return "";
		}
	}
}
