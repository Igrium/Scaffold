package org.scaffoldeditor.scaffold.level.entity.world;

import java.util.Set;

import org.joml.Vector3ic;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.block.WorldMath.SectionCoordinate;
import org.scaffoldeditor.scaffold.level.Level;

/**
 * Represents any kind of world entity representing a single block.
 * @author Igrium
 *
 */
public abstract class BaseSingleBlock extends BaseBlockEntity {

	public BaseSingleBlock(Level level, String name) {
		super(level, name);
	}
	
	/**
	 * Get the block that this entity will place.
	 */
	public abstract Block getBlock();

	@Override
	public boolean compileWorld(BlockWorld world, boolean full, Set<SectionCoordinate> sections) {
		Vector3ic blockPos = getBlockPosition();
		world.setBlock(blockPos.x(), blockPos.y(), blockPos.z(), getBlock(), this);
		return true;
	}

	@Override
	public Block blockAt(Vector3ic coord) {
		if (coord.equals(getBlockPosition())) return getBlock();
		else return null;
	}

	@Override
	public Vector3ic[] getBounds() {
		return new Vector3ic[] { getBlockPosition(), getBlockPosition() };
	}

	@Override
	public BlockCollection getBlockCollection() {
		return SizedBlockCollection.singleBlock(getBlock());
	}
}
