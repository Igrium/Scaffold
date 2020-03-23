package org.scaffoldeditor.scaffold.level.entity.block;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Faceable;
import org.scaffoldeditor.scaffold.math.Vector;

/**
 * Represents a BlockCollection in the level
 * @author Sam54123
 */
public abstract class BlockCollectionEntity extends Faceable implements BlockEntity {
	
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
		
		if (blockCollection == null) {
			return true;
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
	
	@Override
	public Block blockAt(Vector coord) {
		if (blockCollection == null) {
			return null;
		}
		
		int x = (int) Math.floor(coord.X());
		int y = (int) Math.floor(coord.Y());
		int z = (int) Math.floor(coord.Z());
		
		// Make sure block is in bounds.
		if (Math.abs(x) < blockCollection.getWidth() / 2
				&& Math.abs(y) < blockCollection.getHeight() / 2
				&& Math.abs(z) < blockCollection.getLength() / 2) {
			
			if (!blockCollection.blockAt(x, y, z).getName().equals("minecraft:structure_void")) {
				return blockCollection.blockAt(x, y, z);
			} else {
				return null;
			}
		} 
		return null;
	}

	/**
	 * Reloads the block collection (usually from file).
	 * @param force Force it to reload, no matter what.
	 */
	public abstract void reload(boolean force);

}
