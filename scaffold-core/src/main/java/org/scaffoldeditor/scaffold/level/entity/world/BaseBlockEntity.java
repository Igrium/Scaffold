package org.scaffoldeditor.scaffold.level.entity.world;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;

/**
 * Implements many of the common features of block entities.
 * @author Igrium
 */
public abstract class BaseBlockEntity extends Entity implements BlockEntity {

	public BaseBlockEntity(Level level, String name) {
		super(level, name);	
	}
	
	/**
	 * Whether a recent change to attributes requires the block collection to recompile.
	 * <br>
	 * If unsure, just return true. This should only be called if the attributes have actually
	 * been updated.
	 * <br>
	 * <b>Note:</b> Calling <code>getPosition()</code> or accessing any attributes in this method
	 * will retrieve the new values.
	 */
	public abstract boolean needsRecompiling();
	
	@Override
	public void onUpdateAttributes(boolean noRecompile) {
		// Changing the attributes
		getLevel().dirtySections.addAll(getOverlappingSections());
		super.onUpdateAttributes(noRecompile);
		onUpdateBlockAttributes();
		getLevel().dirtySections.addAll(getOverlappingSections());
		
		if (getLevel().autoRecompile && !noRecompile) {
			getLevel().quickRecompile();
		}
	};

	/**
	 * Called when it's time to update the blocks in this entity during an attribute update.
	 * Implementations of this method do not need to mark sections as dirty, however, bounding box
	 * updates should be calculated. 
	 * <br>
	 * SHOULD NOT CALL <code>compileWorld()</code>!
	 * <br>
	 * <b>Note:</b> the entity may have moved without calling <code>setPosition()</code> when this is called.
	 */
	public abstract void onUpdateBlockAttributes();
}
