package org.scaffoldeditor.scaffold.level.io;

import java.util.List;

/**
 * Allows an entity to declare an input that it emits.
 * Doesn't do anything natively except inform the UI.
 * @author Igrium
 */
public interface OutputDeclaration {
	
	/**
	 * Get the output's name.
	 */
	public String getName();
	
	/**
	 * Get the arguements this output will emit to its connected input.
	 * @return A list of attribute registry names.
	 */
	public List<String> getArguements();
}
