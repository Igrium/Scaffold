package org.scaffoldeditor.scaffold.level.entity.block;

import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Faceable;
import org.scaffoldeditor.scaffold.math.Vector;

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
