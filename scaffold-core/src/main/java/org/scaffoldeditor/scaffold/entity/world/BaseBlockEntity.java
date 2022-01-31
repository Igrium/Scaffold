package org.scaffoldeditor.scaffold.entity.world;

import java.util.Map;
import java.util.Set;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.scaffold.entity.BlockEntity;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.render.BlockRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

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
	protected Vector3dc positionCache;
	
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
	protected void onSetAttributes(Map<String, Attribute<?>> updated) {
		if (positionCache != null && updated.containsKey("position")) {
			// Temporarily set the position back so we can capture the bounds.
			Vector3dc newPosition = getPosition();
			setPositionNoUpdate(positionCache);
			getLevel().dirtySections.addAll(getOverlappingSections());
			setPositionNoUpdate(newPosition);
		} else {
			getLevel().dirtySections.addAll(getOverlappingSections());
		}
		updateBlocks();
		getLevel().dirtySections.addAll(getOverlappingSections());
		super.onSetAttributes(updated);
		updateBlocks();
		getLevel().dirtySections.addAll(getOverlappingSections());
		positionCache = getPosition();
	}

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
	public abstract void updateBlocks();

	/**
	 * Obtain a block collection with all of the blocks in this entity. Used for
	 * generating the block hologram used when moving the entity.
	 * 
	 * @return All the blocks in the entity.
	 */
	public abstract BlockCollection getBlockCollection();
	
	@Override
	public Set<RenderEntity> getRenderEntities() {
		Set<RenderEntity> set = super.getRenderEntities();
		if (isTransformPreviewEnabled()) {
			set.add(new BlockRenderEntity(this, getBlockCollection(), getPreviewPosition(), new Vector3d(), "model"));
		}
		return set;
	}
	
	@Override
	public boolean isGridLocked() {
		return true;
	}
}
