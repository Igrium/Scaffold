package org.scaffoldeditor.nbt.block;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.scaffoldeditor.nbt.io.ChunkParser;
import org.scaffoldeditor.nbt.io.WorldInputStream;

import mryurihi.tbnbt.tag.NBTTagCompound;

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
	
	// Iterates over all chunks, and all blocks in the chunks.
	@Override
	public Iterator<Block> iterator() {
		return new Iterator<Block>() {
			
			private Iterator<Chunk> chunksIterator = chunks().iterator();
			private Iterator<Block> chunk = null;
			
			@Override
			public boolean hasNext() {
				return (chunksIterator.hasNext() || (chunk != null && chunk.hasNext()));
			}

			@Override
			public Block next() {
				if (chunk == null || !chunk.hasNext()) {
					chunk = chunksIterator.next().iterator();
				}
				
				return chunk.next();
			}
		};
	}
	
	/**
	 * Read a BlockWorld from a Minecraft save file.
	 * @param regionFolder (Absolute) path to region folder within world folder.
	 * @return Parsed BlockWorld.
	 * @throws IOException If an IOException occurs.
	 * @throws FileNotFoundException If any of the files are not found.
	 */
	public static BlockWorld deserialize(File regionFolder) throws FileNotFoundException, IOException {
		System.out.println("Reading world at "+regionFolder);
		BlockWorld world = new BlockWorld();
		
		File[] regionFiles = regionFolder.listFiles();
		for (File f : regionFiles) {
			if (FilenameUtils.getExtension(f.toString()).matches("mca")) {
				world.parseRegionFile(f);
			}
		}
		
		System.out.println("World read successfully!");
		return world;
	}
	
	/**
	 * Read all the chunks in a region file and add them to this world.
	 * @param regionFile
	 * @throws IOException If an IO exception occurs during file parsing.
	 * @throws FileNotFoundException If the file is not found.
	 */
	private void parseRegionFile(File regionFile) throws FileNotFoundException, IOException {
		System.out.println("Reading "+regionFile);
		List<NBTTagCompound> chunkMaps = new ArrayList<NBTTagCompound>();
		WorldInputStream is = new WorldInputStream(new FileInputStream(regionFile));
		
		// Read all chunks from file.
		while (is.hasNext()) {
			chunkMaps.add(is.readChunkNBT().nbt);
		}
		is.close();
		
		// Add chunks to world.
		for (NBTTagCompound c : chunkMaps) {
			NBTTagCompound level = c.get("Level").getAsTagCompound();
			ChunkCoordinate coord = new ChunkCoordinate(
					level.get("xPos").getAsTagInt().getValue(),
					level.get("zPos").getAsTagInt().getValue());
			
			chunks.put(coord, ChunkParser.parseNBT(level));
			
		}
	}
}
