package org.metaversemedia.scaffold.level;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.metaversemedia.scaffold.nbt.Block;
import org.metaversemedia.scaffold.nbt.BlockCollection;
import org.metaversemedia.scaffold.nbt.SizedBlockCollection;

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
	 * Place a block collection in the world.
	 * Collection origin is the most negitive (coordinate wize) corner.
	 * @param collection Collection to place.
	 * @param x X position.
	 * @param y Y position.
	 * @param z Z position.
	 * @param override Should override existing blocks?
	 */
	public void addBlockCollection(SizedBlockCollection collection, int x, int y, int z, boolean override) {
		for (int Y = 0; Y < collection.getWidth(); Y++) {
			for (int Z = 0; Z < collection.getLength(); Z++) {
				for (int X = 0; X < collection.getHeight(); X++) {
					int globalX = x + X;
					int globalY = y + Y;
					int globalZ = z + Z;
					
					Block oldBlock = blockAt(globalX, globalY, globalZ); // For if override is disabled.
					Block newBlock = collection.blockAt(X, Y, Z);
					
					if (newBlock != null &&
							(override || oldBlock == null || oldBlock.getName().matches("minecraft:air"))) {
						setBlock(globalX, globalY, globalZ, newBlock);
					}
				}
			}
		}
	}
	
	/**
	 * Place a block collection in the world.
	 * Collection origin is the most negitive (coordinate wize) corner.
	 * @param collection Collection to place.
	 * @param x X position.
	 * @param y Y position.
	 * @param z Z position.
	 */
	public void addBlockCollection(SizedBlockCollection collection, int x, int y, int z) {
		addBlockCollection(collection, x, y, z, false);
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
	
	/**
	 * Clear the BlockWorld of all chunks and blocks.
	 */
	public void clear() {
		chunks.clear();
	}
}
