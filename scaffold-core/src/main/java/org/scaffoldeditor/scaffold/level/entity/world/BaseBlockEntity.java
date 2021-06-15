package org.scaffoldeditor.scaffold.level.entity.world;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.math.Vector;

/**
 * Implements many of the common features of block entities.
 * @author Igrium
 */
public abstract class BaseBlockEntity extends Entity implements BlockEntity {

	public BaseBlockEntity(Level level, String name) {
		super(level, name);	
	}
	
	/**
	 * Cache the position of the entity so we can use the old position when attributes are updated.
	 */
	protected Vector positionCache;
	
	/**
	 * <p>
	 * Whether the most recent change to attributes requires the block collection to
	 * recompile.
	 * </p>
	 * <p>
	 * If unsure, just return true. This should only be called if the attributes
	 * have actually been updated.
	 * </p>
	 * <b>Note:</b> Calling {@link #getPosition()} or accessing any attributes in
	 * this method will retrieve the new values. If you need to access the old
	 * values, you should maintain a cache.
	 */
	protected abstract boolean needsRecompiling();
	
	@Override
	public void onUpdateAttributes(boolean noRecompile) {
		// Changing the attributes
		Vector newPosition = getPosition();
		if (positionCache != null && !newPosition.equals(positionCache)) {
			// Temporarily set the position back so we can capture the bounds.
			setAttribute("position", new VectorAttribute(positionCache), true);
			getLevel().dirtySections.addAll(getOverlappingSections());
			setAttribute("position", new VectorAttribute(newPosition), true);
		} else {
			getLevel().dirtySections.addAll(getOverlappingSections());
		}
		super.onUpdateAttributes(true);
		onUpdateBlockAttributes();
		getLevel().dirtySections.addAll(getOverlappingSections());
		positionCache = newPosition;
		if (getLevel().autoRecompile && !noRecompile) {
			getLevel().quickRecompile();
		}
	};

	/**
	 * Called when it's time to update variables that determine the placed blocks
	 * during an attribute update, typically to reload models from disk, etc. Not to
	 * be confused with {@link #compileWorld(BlockWorld, boolean)}, which does the actual placing of the
	 * blocks. Implementations of this method do not need to mark sections as dirty,
	 * however, bounding box updates should be calculated. <br>
	 * SHOULD NOT CALL <code>compileWorld()</code>! <br>
	 * <b>Note:</b> the entity may have moved without calling
	 * <code>setPosition()</code> when this is called.
	 */
	public abstract void onUpdateBlockAttributes();
}
