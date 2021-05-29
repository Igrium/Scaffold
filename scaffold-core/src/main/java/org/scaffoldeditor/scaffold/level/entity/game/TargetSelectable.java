package org.scaffoldeditor.scaffold.level.entity.game;

/**
 * Represents an entity that has a runtime presence in Minecraft and can be selected with target selectors.
 * @author Igrium
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
