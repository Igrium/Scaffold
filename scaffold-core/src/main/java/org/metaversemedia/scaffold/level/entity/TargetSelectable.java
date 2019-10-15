package org.metaversemedia.scaffold.level.entity;

/**
 * Represents an entity that has a runtime presence in Minecraft and can be selected with target selectors.
 * @author Sam54123
 */
public interface TargetSelectable {
	
	/**
	 * Get the target selector commands can refer to this entity with.
	 * In the form [[target selector]].
	 * MAY ONLY RETURN ONE ENTITY. Use limit to enforce.
	 * @return
	 */
	public String getTargetSelector();
}
