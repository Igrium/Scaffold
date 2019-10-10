package org.metaversemedia.scaffold.level;

import java.util.HashMap;
import java.util.Map;

import org.metaversemedia.scaffold.nbt.Block;
import org.metaversemedia.scaffold.nbt.BlockCollection;

/**
 * Represents all the blocks in a world.
 * @author Sam54123
 */
public class BlockWorld implements BlockCollection {

	// Chunks are stored in a map with a 2 index long array of their coordinates
	private Map<int[], Chunk> chunks = new HashMap<int[], Chunk>();
	
	@Override
	public Block blockAt(int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setBlock(int x, int y, int z, Block block) {
		if (y < 0 || y > Chunk.HEIGHT) {
			throw new IllegalArgumentException("Block Y value must be between 0 and "+Chunk.HEIGHT);
		}
		
		// Get chunk to place in
		int[] chunkKey = new int[] {
				(int) Math.floor(x/Chunk.WIDTH),
				(int) Math.floor(z/Chunk.WIDTH)};
		
		Chunk chunk = null;
		if (chunks.containsKey(chunkKey)) {
			chunk = chunks.get(chunkKey);
		} else {
			chunk = new Chunk();
			chunks.put(chunkKey, chunk);
		}
		
		chunk.setBlock(x % Chunk.WIDTH, y, z % Chunk.LENGTH, block);
	}
		
	
	/**
	 * Get the chunk at a specific X and Z coordinate.
	 * Coordinates are chunk coordinates (block coordinate / chunk size).
	 * @param x X coordinate.
	 * @param z Z coordinate.
	 * @return Chunk at coordinates.
	 */
	public Chunk chunkAt(int x, int z) {
		return chunks.get(new int[] {x,z});
	}

}
