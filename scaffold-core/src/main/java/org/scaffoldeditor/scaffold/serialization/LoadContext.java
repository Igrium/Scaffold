package org.scaffoldeditor.scaffold.serialization;

import java.util.HashMap;
import java.util.Map;

/**
 * Keep track of various global variables required for deserialization. Lifetime
 * only lasts the duration of deserialization.
 * 
 * @author Igrium
 *
 */
public class LoadContext {
	/**
	 * All the entities that had to be renamed for whatever reason during the load process.
	 */
	public final Map<String, String> renamedEnts = new HashMap<>();
}
