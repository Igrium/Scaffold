package org.metaversemedia.scaffold.level.entity;

import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.nbt.BlockCollection;

/**
 * Represents a BlockCollection in the level
 * @author Sam54123
 */
public abstract class BlockCollectionEntity extends Faceable {
	
	protected BlockCollection blockCollection;

	public BlockCollectionEntity(Level level, String name) {
		super(level, name);
	}
	
	/**
	 * Retrieve this entity's BlockCollection
	 * @return BlockCollection object
	 */
	public BlockCollection getBlockCollection() {
		return blockCollection;
	}
	
	
	
	@Override
	protected void onUpdateAttributes() {
		super.onUpdateAttributes();
		reload(false);
	}

	/**
	 * Reloads the block collection (usually from file).
	 * @param force Force it to reload, no matter what.
	 */
	public abstract void reload(boolean force);

}
