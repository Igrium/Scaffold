package org.metaversemedia.scaffold.level.entity;

import org.metaversemedia.scaffold.level.BlockWorld;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.math.Vector;
import org.metaversemedia.scaffold.nbt.SizedBlockCollection;

/**
 * Represents a BlockCollection in the level
 * @author Sam54123
 */
public abstract class BlockCollectionEntity extends Faceable {
	
	protected SizedBlockCollection blockCollection;

	public BlockCollectionEntity(Level level, String name) {
		super(level, name);
	}
	
	/**
	 * Retrieve this entity's BlockCollection
	 * @return BlockCollection object
	 */
	public SizedBlockCollection getBlockCollection() {
		return blockCollection;
	}
	
	
	
	@Override
	protected void onUpdateAttributes() {
		super.onUpdateAttributes();
		reload(false);
	}
	
	@Override
	public boolean compileWorld(BlockWorld blockWorld, boolean full) {
		if  (!super.compileWorld(blockWorld, full)) {
			return false;
		}
		
		// Suggest reload if full compile.
		if (full) {
			reload(false);
		}
		
		Vector position = getPosition();
		
		int x = (int) Math.floor(position.X());
		int y = (int) Math.floor(position.Y());
		int z = (int) Math.floor(position.Z());
		
		blockWorld.addBlockCollection(getBlockCollection(), x, y, z);
		
		return true;
	}

	/**
	 * Reloads the block collection (usually from file).
	 * @param force Force it to reload, no matter what.
	 */
	public abstract void reload(boolean force);

}
