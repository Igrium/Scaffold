package org.scaffoldeditor.nbt.block;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.nbt.math.Vector3i;

import net.querz.nbt.tag.CompoundTag;

/**
 * Represents a generic block collection intended for easy writing to from code.
 * Does not represent a file format and cannot be serialized.
 * 
 * @author Igrium
 */
public class GenericBlockCollection implements BlockCollection {
	
	private Map<Vector3i, Block> placedBlocks = new HashMap<>();
	private Map<Vector3i, CompoundTag> blockEntities = new HashMap<>();

	@Override
	public Iterator<Vector3i> iterator() {
		return placedBlocks.keySet().iterator();
	}

	@Override
	public Block blockAt(int x, int y, int z) {
		return placedBlocks.get(new Vector3i(x, y, z));
	}
	
	/**
	 * Set the block at a particular location.
	 * @param pos Local position.
	 * @param block Block to place.
	 */
	public void setBlock(Vector3i pos, Block block) {
		placedBlocks.put(pos, block);
	}
	
	/**
	 * Set the block at a particular location.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate.
	 * @param block Block to place.
	 */
	public void setBlock(int x, int y, int z, Block block) {
		placedBlocks.put(new Vector3i(x, y, z), block);
	}
	
	@Override
	public CompoundTag blockEntityAt(Vector3i vec) {
		return blockEntities.get(vec);
	}
	
	/**
	 * Place a block entity at a certian position.
	 * @param pos Local position.
	 * @param ent Block entity NBT.
	 */
	public void setBlockEntity(Vector3i pos, CompoundTag ent) {
		blockEntities.put(pos, ent);
	}
	
	@Override
	public Set<Vector3i> getBlockEntities() {
		return blockEntities.keySet();
	}
}
