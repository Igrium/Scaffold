package org.scaffoldeditor.scaffold.logic.datapack;

/**
 * Represents a single Minecraft command.
 * @author Igrium
 */
public interface Command {
	
	/**
	 * Compile this command into a callable string.
	 * @return Command string (withing the leading slash).
	 */
	public String compile();
	
	/**
	 * Create a command object from a string.
	 * @param in Command string (without the leading slash).
	 * @return Command object.
	 */
	public static Command fromString(String in) {
		return new Command() {
			
			@Override
			public String compile() {
				return in;
			}
		};
	}
}
