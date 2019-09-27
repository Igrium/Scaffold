package org.metaversemedia.scaffold.nbt.schematic;

import java.util.List;

import org.metaversemedia.scaffold.nbt.Block;
import org.metaversemedia.scaffold.nbt.BlockCollection;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.ListTag;

/**
 * Represents a Minecraft structure schematic (.nbt)
 * @author Sam54123
 */
public class Structure implements BlockCollection {
	
	private CompoundMap[] palette;
	private CompoundMap[] blocks;
	private List<CompoundMap> entities;
	
	private int sizeX;
	private int sizeY;
	private int sizeZ;
	
	/**
	 * Get the name of the block at a particular location.
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block
	 */
	@Override
	public Block blockAt(int x, int y, int z) {
		// Look for block with matching coords
		for (CompoundMap block : blocks) {
			ListTag<IntTag> coordTag = (ListTag<IntTag>) block.get("pos");
			List<IntTag> coords = coordTag.getValue();
			
			if (coords.get(0).getValue() == x &&
					coords.get(1).getValue() == y &&
					coords.get(2).getValue() == z) {
				
				// Get block from palette
				CompoundMap palleteBlock = palette[(int) block.get("state").getValue()];
				return new Block(
						(String) palleteBlock.get("Name").getValue(),
						(CompoundMap) palleteBlock.get("Properties").getValue()
					);
			}
		}
		return null;
	}

	@Override
	public int sizeX() {
		return sizeX;
	}

	@Override
	public int sizeY() {
		// TODO Auto-generated method stub
		return sizeY;
	}

	@Override
	public int sizeZ() {
		// TODO Auto-generated method stub
		return sizeZ;
	}

}
