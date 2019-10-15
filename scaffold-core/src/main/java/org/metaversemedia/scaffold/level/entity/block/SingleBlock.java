package org.metaversemedia.scaffold.level.entity.block;


import org.metaversemedia.scaffold.level.BlockWorld;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.nbt.Block;

/**
 * Class for entities that are represented as a single block ingame.
 * @author Sam54123
 */
public abstract class SingleBlock extends Entity {
	
	protected Block block;

	public SingleBlock(Level level, String name) {
		super(level, name);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Get the block's X coordinate.
	 * @return X
	 */
	public int blockX() {
		return (int) Math.floor(getPosition().X());
	}
	
	/**
	 * Get the block's Y coordinate.
	 * @return Y
	 */
	public int blockY() {
		return (int) Math.floor(getPosition().Y());
	}
	
	/**
	 * Get the block's Z coordinate.
	 * @return Z
	 */
	public int blockZ() {
		return (int) Math.floor(getPosition().Z());
	}
	
	/**
	 * Get the entity's block.
	 * @return
	 */
	public Block getBlock() {
		return block;
	}
	
	@Override
	public boolean compileWorld(BlockWorld blockWorld, boolean full) {
		if (!super.compileWorld(blockWorld, full)) {;
			return false;
		}
		
		blockWorld.setBlock(blockX(), blockY(), blockZ(), block);
		return true;
	}
}
