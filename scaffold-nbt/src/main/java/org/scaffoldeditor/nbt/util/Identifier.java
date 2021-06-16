package org.scaffoldeditor.nbt.util;

/**
 * Represents a Minecraft <a
 * href=https://minecraft.fandom.com/wiki/Namespaced_ID>identifier</a>.
 * 
 * @author Igrium
 *
 */
public class Identifier {
	
	public final String namespace;
	public final String value;

	public Identifier(String namespace, String value) {
		this.namespace = namespace;
		this.value = value;
	}
	
	public Identifier(String name) {
		String[] split = name.split(":", 1);
		namespace = split[0];
		value = split[1];
	}
	
	/**
	 * Get this identifier as represented by a string, with its namespace and value
	 * seperated by a colon.
	 * 
	 * @return [namespace]:[value]
	 */
	public String toString() {
		return namespace+":"+value;
	}
	
	public SingleTypePair<String> toPair() {
		return new SingleTypePair<>(namespace, value);
	}
}
