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

}
