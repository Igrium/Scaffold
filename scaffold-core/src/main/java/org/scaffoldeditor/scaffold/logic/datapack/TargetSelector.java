package org.scaffoldeditor.scaffold.logic.datapack;

/**
 * Represents a Minecraft target selector.
 * @author Igrium
 */
public interface TargetSelector {
	
	/**
	 * Get the target selector string.
	 * @return String in the format <code>@e[name=...]</code>
	 */
	public String compile();
	
	/**
	 * Create a target selector from a string.
	 * @param in Target selector string in the format <code>@e[name=...]</code>
	 * @return Target selector.
	 */
	public static TargetSelector fromString(String in) {
		return new TargetSelector() {
			
			@Override
			public String compile() {
				return in;
			}
		};
	}
}
