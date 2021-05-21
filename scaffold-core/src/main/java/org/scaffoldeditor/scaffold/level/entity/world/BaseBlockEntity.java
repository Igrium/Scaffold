package org.scaffoldeditor.scaffold.level.entity.world;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Faceable;
import org.scaffoldeditor.scaffold.math.Vector;

/**
 * Implements many of the common features of block entities.
 * @author Igrium
 */
public abstract class BaseBlockEntity extends Faceable implements BlockEntity {

	public BaseBlockEntity(Level level, String name) {
		super(level, name);	
	}
	
	@Override
	public void onUpdateAttributes() {
		// Changing the attributes
		getLevel().dirtySections.addAll(getOverlappingSections(getWorld()));
		super.onUpdateAttributes();
		updateBlockAttributes();
		getLevel().dirtySections.addAll(getOverlappingSections(getWorld()));
		
		if (getLevel().autoRecompile) {
			getLevel().quickRecompile();
		}
	};
	
	@Override
	public void setPosition(Vector position) {
		getLevel().dirtySections.addAll(getOverlappingSections(getLevel().getBlockWorld()));
		super.setPosition(position);
		getLevel().dirtySections.addAll(getOverlappingSections(getLevel().getBlockWorld()));
		
		if (getLevel().autoRecompile) {
			getLevel().quickRecompile();
		}
	}
	
	/**
	 * Called when it's time to update the blocks in this entity during an attribute update.
	 * Implementations of this method do not need to mark sections as dirty, however, bounding box
	 * updates should be calculated.
	 * <br>
	 * <b>Note:</b> the entity may have moved without calling <code>setPosition</code> when this is called.
	 */
	public abstract void updateBlockAttributes();
}
