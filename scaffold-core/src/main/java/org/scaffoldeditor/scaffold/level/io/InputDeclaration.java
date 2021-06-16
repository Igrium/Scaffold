package org.scaffoldeditor.scaffold.level.io;

import java.util.Collections;
import java.util.List;

/**
 * Allows an entity to declare an input that it takes.
 * Doesn't do anything natively except inform the UI.
 * @author Igrium
 */
public interface InputDeclaration {
	/**
	 * Get this input's name.
	 */
	public String getName();
	
	/**
	 * Get the arguements this input expects to take.
	 * @return A list of attribute registry names.
	 */
	default List<String> getArguements() {
		return Collections.emptyList();
	}
}
