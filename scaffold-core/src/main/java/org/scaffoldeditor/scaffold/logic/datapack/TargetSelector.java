package org.scaffoldeditor.scaffold.logic.datapack;

import java.util.UUID;

/**
 * Represents a Minecraft target-selector-like object. May be a target selector,
 * a UUID, or a player name.
 * 
 * @author Igrium
 */
public interface TargetSelector {
	
	public static final TargetSelector SELF = fromString("@s");
	
	/**
	 * Get the target selector string.
	 * 
	 * @return Target selector string that can be inserted into commands.
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
	
	/**
	 * Create a target selector that references an entity with a specific UUID.
	 * @param in UUID to use.
	 * @return Target selector.
	 */
	public static TargetSelector fromUUID(UUID in) {
		return TargetSelector.fromString(in.toString());
	}
	
}
