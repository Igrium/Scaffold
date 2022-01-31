package org.scaffoldeditor.scaffold.entity.game;

import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;

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
	public TargetSelector getTargetSelector();
	
	public static TargetSelectable wrap(TargetSelector in) {
		return new TargetSelectable() {
			
			@Override
			public TargetSelector getTargetSelector() {
				return in;
			}
		};
	}
}
