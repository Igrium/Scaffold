package org.scaffoldeditor.nbt.block;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FilenameUtils;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.io.ChunkParser;
import org.scaffoldeditor.nbt.io.WorldInputStream;
import org.scaffoldeditor.nbt.io.WorldOutputStream;

import net.querz.nbt.tag.CompoundTag;

/**
 * Represents all the blocks in a world.
 * @author Sam54123
 */
public class BlockWorld implements BlockCollection {
	
	/**
	 * Class to represent chunk coordinate pairs.
	 * @author Sam54123
	 */
	public static class ChunkCoordinate implements Comparable<ChunkCoordinate> {
		public final int x;
		public final int z;
		
		public ChunkCoordinate(int x, int z) {
			this.x = x;
			this.z = z;
		}
		
		public ChunkCoordinate(SectionCoordinate c) {
			this.x = c.x;
			this.z = c.z;
		}
		
		public int x() {
			return x;
		}
		
		public int z() {
			return z;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ChunkCoordinate)) {
				return false;
			}
			ChunkCoordinate chunkCoordinate = (ChunkCoordinate) obj;
			return chunkCoordinate.x() == x && chunkCoordinate.z() == z;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(x, z);
		}

		@Override
		public int compareTo(ChunkCoordinate o) {
			if (z == o.z) {
				return x - o.x;
			} else {
				return z - o.z;
			}
		}
		
		@Override
		public String toString() {
			return "["+x+", "+z+"]";
		}
	}

	// Chunks are stored in a map with a 2 index long array of their coordinates
	private Map<ChunkCoordinate, Chunk> chunks = new HashMap<ChunkCoordinate, Chunk>();
	
	@Override
	public Block blockAt(int x, int y, int z) {
		if (y < 0 || y > Chunk.HEIGHT) {
			return null;
		}
		
		// Find chunk block is in
		ChunkCoordinate chunkKey = new ChunkCoordinate(
				(int) Math.floor((double) x/Chunk.WIDTH),
				(int) Math.floor((double) z/Chunk.WIDTH));
		
		Chunk chunk = null;
		if (chunks.containsKey(chunkKey)) {
			chunk = chunks.get(chunkKey);
		} else {
			return null;
		}
		
		// Convert into chunk coordinates.
		int chunkX = x % Chunk.WIDTH;
		int chunkZ = z % Chunk.LENGTH;
		
		if (chunkX < 0) {
			chunkX = Chunk.WIDTH + chunkX;
		}
		if (chunkZ < 0) {
			chunkZ = Chunk.LENGTH + chunkZ;
		}
		
		return chunk.blockAt(chunkX, y, chunkZ);
	}
	
	public Object getBlockOwner(int x, int y, int z) {
		// Find chunk block is in
		ChunkCoordinate chunkKey = new ChunkCoordinate((int) Math.floor((double) x / Chunk.WIDTH),
				(int) Math.floor((double) z / Chunk.WIDTH));

		Chunk chunk = null;
		if (chunks.containsKey(chunkKey)) {
			chunk = chunks.get(chunkKey);
		} else {
			return null;
		}

		// Convert into chunk coordinates.
		int chunkX = x % Chunk.WIDTH;
		int chunkZ = z % Chunk.LENGTH;

		if (chunkX < 0) {
			chunkX = Chunk.WIDTH + chunkX;
		}
		if (chunkZ < 0) {
			chunkZ = Chunk.LENGTH + chunkZ;
		}
		
		return chunk.getOwner(chunkX, y, chunkZ);
	}
	
	/**
	 * Set the block at a particular location.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate.
	 * @param block Block to set.
	 */
	public void setBlock(int x, int y, int z, Block block) {
		setBlock(x, y, z, block, null);
	}
	
	/**
	 * Set the block at a particular location.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate.
	 * @param owner Owner object to attribute the block to.
	 * @param block Block to set.
	 */
	public void setBlock(int x, int y, int z, Block block, Object owner) {
		if (y < 0 || y > Chunk.HEIGHT) {
			return;
		}
		// Get chunk to place in
		ChunkCoordinate chunkKey = new ChunkCoordinate(
				(int) Math.floor(x / (double) Chunk.WIDTH),
				(int) Math.floor(z / (double) Chunk.LENGTH));
		
		Chunk chunk = null;
		if (chunks.containsKey(chunkKey)) {
			chunk = chunks.get(chunkKey);
		} else {
			chunk = new Chunk();
			chunks.put(chunkKey, chunk);
		}
		
		int relativeX = x - chunkKey.x * Chunk.WIDTH;
		int relativeZ = z - chunkKey.z * Chunk.LENGTH;
				
		chunk.setBlock(relativeX, y, relativeZ, block, owner);
	}
	
	/**
	 * Place a block collection in the world.
	 * Collection origin is the most negitive (coordinate wize) corner.
	 * @param collection Collection to place.
	 * @param x X position.
	 * @param y Y position.
	 * @param z Z position.
	 * @param z Owner to assign blocks to.
	 * @param override Should override existing blocks?
	 */
	public void addBlockCollection(SizedBlockCollection collection, int x, int y, int z, boolean override, Object owner) {
		for (int Y = 0; Y < collection.getHeight(); Y++) {
			for (int Z = 0; Z < collection.getLength(); Z++) {
				for (int X = 0; X < collection.getWidth(); X++) {
					int globalX = x + X;
					int globalY = y + Y;
					int globalZ = z + Z;
					
					Block oldBlock = blockAt(globalX, globalY, globalZ); // For if override is disabled.
					Block newBlock = collection.blockAt(X, Y, Z);
					
					if (newBlock != null &&
							(override || oldBlock == null || oldBlock.getName().matches("minecraft:air"))) {
						setBlock(globalX, globalY, globalZ, newBlock, owner);
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
	 * @param override Should override existing blocks?
	 */
	public void addBlockCollection(SizedBlockCollection collection, int x, int y, int z, boolean override) {
		addBlockCollection(collection, x, y, z, override, null);
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
		addBlockCollection(collection, x, y, z, false, null);
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
	
	public Section sectionAt(int x, int y, int z) {
		return chunks.get(new ChunkCoordinate(x,z)).sections[Math.floorDiv(y, Section.HEIGHT)];
	}
	
	/**
	 * Get a collection of all this world's chunks.
	 * @return Chunks.
	 */
	public Collection<Chunk> chunks() {
		return chunks.values();
	}
	
	/**
	 * Get all this worlds chunks.
	 * @return A map with chunk coordinates as keys and their corresponding chunks as values.
	 */
	public Map<ChunkCoordinate, Chunk> getChunks() {
		return this.chunks;
	}
	
	/**
	 * Clear the BlockWorld of all chunks and blocks.
	 */
	public void clear() {
		chunks.clear();
	}
	
	/**
	 * Get a collection of all the entities in the world.
	 * @return All the entities, represented by compound maps in the <a href="https://minecraft.gamepedia.com/Chunk_format#entity_format">entity format</a>
	 */
	public Collection<CompoundTag> entities() {
		Collection<CompoundTag> entities = new ArrayList<>();
		for (Chunk c : chunks.values()) {
			entities.addAll(c.entities);
		}
		return entities;
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
				System.out.println("Parsing region file "+f.toString());
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
	public void parseRegionFile(File regionFile) throws FileNotFoundException, IOException {
		System.out.println("Reading "+regionFile);
		List<CompoundTag> chunkMaps = new ArrayList<>();
		WorldInputStream is = new WorldInputStream(new FileInputStream(regionFile));
		
		// Read all chunks from file.
		while (is.hasNext()) {
			chunkMaps.add(is.readChunkNBT().nbt);
		}
		is.close();
		
		// Add chunks to world.
		for (int i = 0; i < chunkMaps.size(); i++) {
			System.out.println("Parsing chunk "+i+"/"+chunkMaps.size());
			CompoundTag c = chunkMaps.get(i);
			CompoundTag level = c.getCompoundTag("Level");
			ChunkCoordinate coord = new ChunkCoordinate(
					level.getInt("xPos"),
					level.getInt("zPos"));
			
			chunks.put(coord, ChunkParser.parseNBT(level));
			
		}
	}
	
	/**
	 * Write this BlockWorld to a Minecraft save file.
	 * @param regionFolder Region folder of Minecraft world to write to.
	 * @param dataVersion The <a href="https://minecraft.gamepedia.com/Data_version">data version</a> to use.
	 * @throws IOException If an IO Exception occurs.
	 */
	public void serialize(File regionFolder, int dataVersion) throws IOException {
		System.out.println("Writing world...");
		
		List<ChunkCoordinate> regions = new ArrayList<ChunkCoordinate>();
		
		// Figure out which regions need to be written.
		for (ChunkCoordinate coord : chunks.keySet()) {
			int regionX = (int) Math.floor(coord.x / 32.0);
			int regionZ = (int) Math.floor(coord.z / 32.0);
			
			ChunkCoordinate region = new ChunkCoordinate(regionX, regionZ);
			if (!regions.contains(region)) regions.add(region);
		}
		
		for (ChunkCoordinate region : regions) {
			File regionFile = new File(regionFolder, "r."+region.x+"."+region.z+".mca");
			writeRegionFile(regionFile, region.x, region.z, dataVersion);
		}
	}
	
	/**
	 * Write the appropriate chunks from this world into a region file. (.mca)
	 * @param regionFile File to write to. Will replace if already exists.
	 * @param xOffset X coordinate of the region file.
	 * @param xOffset Z coordinate of the region file.
	 * @param dataVersion Data version of Minecraft version to save to.
	 * @throws IOException 
	 */
	public void writeRegionFile(File regionFile, int xOffset, int zOffset, int dataVersion) throws IOException {
		
		System.out.println("Writing region file " + regionFile.getName());
		
		ChunkParser parser = new ChunkParser(dataVersion);
		
		// Keep track of all the chunks that belong in this file.
		Map<ChunkCoordinate, CompoundTag> chunks = new HashMap<>();
		for (ChunkCoordinate chunkCoord : this.chunks.keySet()) {
			int relativeX = chunkCoord.x - xOffset*32;
			int relativeZ = chunkCoord.z - zOffset*32;
			
			if (0 <= relativeX && relativeX < 32 && 0 <= relativeZ && relativeZ < 32) {
				// Serialize chunk to NBT.
				chunks.put(chunkCoord, parser.writeNBT(this.chunks.get(chunkCoord), chunkCoord.x, chunkCoord.z));
			}
		}
		// Don't write file if there are no chunks.
		if (chunks.size() < 1) {
			return;
		}
		// Override old file.
		if (regionFile.exists()) {
			regionFile.delete();
		}
		
		WorldOutputStream wos = new WorldOutputStream(new FileOutputStream(regionFile), new ChunkCoordinate(xOffset, zOffset));
		
		for (ChunkCoordinate coord : chunks.keySet()) {
			
			// Convert the coordinates into region space.
			int relativeX = coord.x - xOffset*32;
			int relativeZ = coord.z - zOffset*32;
			
			wos.write(new ChunkCoordinate(relativeX, relativeZ), chunks.get(coord));
		}
		
		wos.close();
	}
}
