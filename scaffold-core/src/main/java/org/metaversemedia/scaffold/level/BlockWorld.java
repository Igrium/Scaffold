package org.metaversemedia.scaffold.level;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.metaversemedia.scaffold.nbt.Block;
import org.metaversemedia.scaffold.nbt.BlockCollection;

/**
 * Represents all the blocks in a world.
 * @author Sam54123
 */
public class BlockWorld implements BlockCollection {
	
	/**
	 * Class to represent chunk coordinates in map key.
	 * @author Sam54123
	 */
	private class ChunkCoordinate {
		private int x;
		private int z;
		
		public ChunkCoordinate(int x, int z) {
			this.x = x;
			this.z = z;
		}
		
		public int x() {
			return x;
		}
		
		public int z() {
			return z;
		}
		
		@Override
		public boolean equals(Object obj) {
			ChunkCoordinate chunkCoordinate = (ChunkCoordinate) obj;
			if (chunkCoordinate == null) {return false;}
			return chunkCoordinate.x() == x && chunkCoordinate.z() == z;
		}
		
		@Override
		public int hashCode() {
			return x*10007 + z;
		}
	}

	// Chunks are stored in a map with a 2 index long array of their coordinates
	private Map<ChunkCoordinate, Chunk> chunks = new HashMap<ChunkCoordinate, Chunk>();
	
	@Override
	public Block blockAt(int x, int y, int z) {
		if (y < 0 || y > Chunk.HEIGHT) {
			throw new IllegalArgumentException("Block Y value must be between 0 and "+Chunk.HEIGHT);
		}
				
		// Find chunk block is in
		ChunkCoordinate chunkKey = new ChunkCoordinate(
				(int) Math.floor(x/Chunk.WIDTH),
				(int) Math.floor(z/Chunk.WIDTH));
		
		Chunk chunk = null;
		if (chunks.containsKey(chunkKey)) {
			chunk = chunks.get(chunkKey);
		} else {
			System.out.println("No chunk!");
			return null;
		}
		
		return chunk.blockAt(x % Chunk.WIDTH, y, z % Chunk.LENGTH);
	}
	
	/**
	 * Set the block at a particular location.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate.
	 * @param block Block to set.
	 */
	public void setBlock(int x, int y, int z, Block block) {
		if (y < 0 || y > Chunk.HEIGHT) {
			throw new IllegalArgumentException("Block Y value must be between 0 and "+Chunk.HEIGHT);
		}
		// Get chunk to place in
		ChunkCoordinate chunkKey = new ChunkCoordinate(
				(int) Math.floor(x/Chunk.WIDTH),
				(int) Math.floor(z/Chunk.WIDTH));
		
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
		return chunks.get(new ChunkCoordinate(x,z));
	}
	
	/**
	 * Get a collection of all this world's chunks.
	 * @return Chunks.
	 */
	public Collection<Chunk> chunks() {
		return chunks.values();
	}
}
