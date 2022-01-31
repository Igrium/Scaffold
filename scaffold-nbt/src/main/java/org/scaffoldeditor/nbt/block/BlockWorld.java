package org.scaffoldeditor.nbt.block;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.scaffoldeditor.nbt.block.WorldMath.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.WorldMath.SectionCoordinate;
import org.scaffoldeditor.nbt.io.ChunkParser;
import org.scaffoldeditor.nbt.io.WorldInputStream;
import org.scaffoldeditor.nbt.io.WorldOutputStream;
import org.scaffoldeditor.nbt.math.MathUtils;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.ListTag;

/**
 * Represents all the blocks in a world.
 * @author Igrium
 */
public class BlockWorld implements ChunkedBlockCollection {
	
	private static Logger LOGGER = LogManager.getLogger();

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
	 * Remove a section and all the blocks in it from the world. Also removes tile
	 * entities.
	 * 
	 * @param coord Section to remove.
	 */
	public void clearSection(SectionCoordinate coord) {
		Chunk chunk = chunkAt(coord.x(), coord.z());
		if (chunk == null) return;
		chunk.clearSection(coord.y());
	}
	
	/**
	 * Place a block collection in the world.
	 * 
	 * @param collection Collection to place.
	 * @param x          X position.
	 * @param y          Y position.
	 * @param z          Z position.
	 * @param override   Should override existing blocks?
	 * 
	 * @param placeAir   If this and <code>override</code> are true, air blocks in
	 *                   the collection will override existing blocks. Note that the
	 *                   air block must be explicitally defined in the collection.
	 *                   Places where {@link BlockCollection#blockAt} returns null
	 *                   rather than <code>minecraft:air</code> will not exhibit
	 *                   this behavior.
	 * @param owner      Owner to assign blocks to.
	 */
	public void addBlockCollection(BlockCollection collection, int x, int y, int z, boolean override, boolean placeAir, Object owner) {
		Vector3i targetCoord = new Vector3i(x, y, z);
		LogManager.getLogger().debug("Adding block collection...");
		for (Vector3ic coord : collection) {
			int globalX = x + coord.x();
			int globalY = y + coord.y();
			int globalZ = z + coord.z();
			
			Block oldBlock = blockAt(globalX, globalY, globalZ); // For if override is disabled.
			Block newBlock = collection.blockAt(coord);
			
			if (newBlock != null
					&& (override || oldBlock == null || oldBlock.getName().matches("minecraft:air"))
					&& (placeAir || !newBlock.getName().matches("minecraft:air"))) {
				setBlock(globalX, globalY, globalZ, newBlock, owner);
			}
		}
		
		// Block entities
		for (Vector3ic ent : collection.getBlockEntities()) {
			addBlockEntity(targetCoord.add(ent), collection.blockEntityAt(ent));
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
		addBlockCollection(collection, x, y, z, override, false, null);
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
		addBlockCollection(collection, x, y, z, false, false, null);
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
	 * Get the chunk coordinate of the chunk at a set of block coordinates.
	 * @param x Block X.
	 * @param z Block Z.
	 * @return
	 */
	public ChunkCoordinate chunkAtCoord(int x, int z) {
		return new ChunkCoordinate(
				(int) Math.floor(x / (double) Chunk.WIDTH),
				(int) Math.floor(z / (double) Chunk.LENGTH));
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

	
	public void addEntity(CompoundTag entity, Vector3dc pos) {
		ChunkCoordinate chunkKey = chunkAtCoord((int) pos.x(), (int) pos.z());
		Chunk chunk = chunks.get(chunkKey);
		if (chunk == null) {
			chunk = new Chunk();
			chunks.put(chunkKey, chunk);
		}
		chunk.entities.put(entity, new Vector3d(pos.x() - chunkKey.getStartX(), pos.y(), pos.z() - chunkKey.getStartZ()));
	}
	
	public void removeEntity(CompoundTag entity) {
		ListTag<DoubleTag> posList = entity.getListTag("Pos").asDoubleTagList();
		Vector3i pos = MathUtils.floorVector(new Vector3d(posList.get(0).asDouble(), posList.get(1).asDouble(), posList.get(2).asDouble()));
		
		Chunk chunk = chunks.get(chunkAtCoord(pos.x, pos.z));
		CompoundTag remove = null;
		if (chunk != null) {
			for (CompoundTag nbt : chunk.entities.keySet()) {
				if (nbt.equals(entity)) {
					remove = nbt;
				}
			}
		}
		if (remove != null) chunk.entities.remove(remove);

	}
	
	/**
	 * Add a block entity to the world. Note: block entities continue to exist as
	 * long as the section in which they reside is recompiled.
	 * 
	 * @param pos Global position.
	 * @param nbt NBT of block entity.
	 */
	public void addBlockEntity(Vector3ic pos, CompoundTag nbt) {
		ChunkCoordinate chunkCoord = chunkAtCoord(pos.x(), pos.z());
		Chunk chunk = chunks.get(chunkCoord);
		if (chunk == null) {
			chunk = new Chunk();
			chunks.put(chunkCoord, chunk);
		}
		chunk.blockEntities.put(new Vector3i(pos.x() - chunkCoord.getStartX(), pos.y(), pos.z() - chunkCoord.getStartZ()),
				nbt);
	}

	/**
	 * Get the block entity at a certian location.
	 * 
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate.
	 * @return The block entity nbt, or <code>null</code> of none exists at this
	 *         location.
	 */
	public CompoundTag blockEntityAt(int x, int y, int z) {
		ChunkCoordinate chunkKey = chunkAtCoord(x, z);
		Chunk chunk = chunks.get(chunkKey);
		if (chunk == null) return null;
		
		Vector3i localCoords = chunkKey.relativize(new Vector3i(x, y, z));
		return chunk.blockEntityAt(localCoords);
	}
	
	/**
	 * Read a BlockWorld from a Minecraft save file.
	 * @param regionFolder (Absolute) path to region folder within world folder.
	 * @return Parsed BlockWorld.
	 * @throws IOException If an IOException occurs.
	 * @throws FileNotFoundException If any of the files are not found.
	 */
	public static BlockWorld deserialize(File regionFolder) throws FileNotFoundException, IOException {
		LOGGER.info("Reading world at "+regionFolder);
		BlockWorld world = new BlockWorld();
		
		File[] regionFiles = regionFolder.listFiles();
		for (File f : regionFiles) {
			if (FilenameUtils.getExtension(f.toString()).matches("mca")) {
				LOGGER.info("Parsing region file "+f.toString());
				world.parseRegionFile(f);
			}
		}
		
		LOGGER.info("World read successfully!");
		return world;
	}
	
	/**
	 * Read all the chunks in a region file and add them to this world.
	 * @param regionFile
	 * @throws IOException If an IO exception occurs during file parsing.
	 * @throws FileNotFoundException If the file is not found.
	 */
	public void parseRegionFile(File regionFile) throws FileNotFoundException, IOException {
		LOGGER.info("Reading "+regionFile);
		List<CompoundTag> chunkMaps = new ArrayList<>();
		WorldInputStream is = new WorldInputStream(new FileInputStream(regionFile));
		
		// Read all chunks from file.
		while (is.hasNext()) {
			chunkMaps.add(is.readChunkNBT().nbt);
		}
		is.close();
		
		// Add chunks to world.
		for (int i = 0; i < chunkMaps.size(); i++) {
			LOGGER.info("Parsing chunk "+i+"/"+chunkMaps.size());
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
	 * @param entitiesFolder Entities folder of Minecraft world to write to.
	 * @param dataVersion The <a href="https://minecraft.gamepedia.com/Data_version">data version</a> to use.
	 * @throws IOException If an IO Exception occurs.
	 */
	public void serialize(File regionFolder, File entitiesFolder, int dataVersion) throws IOException {
		LOGGER.info("Writing world...");
		
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
			File entityFile = new File(entitiesFolder, "r."+region.x+"."+region.z+".mca");
			writeRegionFile(regionFile, region.x, region.z, dataVersion);
			writeEntityFile(entityFile, region.x, region.z, dataVersion);
		}
	}
	
	/**
	 * Write this BlockWorld to a Minecraft save file.
	 * @param regionFolder Region folder of Minecraft world to write to.
	 * @param dataVersion The <a href="https://minecraft.gamepedia.com/Data_version">data version</a> to use.
	 * @throws IOException If an IO Exception occurs.
	 */
	public void serialize(File regionFolder, int dataVersion) throws IOException {
		serialize(regionFolder, new File(regionFolder.getParent(), "entities"), dataVersion);
	}
	
	/**
	 * Write the appropriate chunks from this world into a region file. (.mca)
	 * 
	 * @param regionFile  File to write to. Will replace if already exists.
	 * @param xOffset     X coordinate of the region file.
	 * @param xOffset     Z coordinate of the region file.
	 * @param dataVersion Data version of Minecraft version to save to.
	 * @throws IOException If an IO Exception occurs.
	 */
	public void writeRegionFile(File regionFile, int xOffset, int zOffset, int dataVersion) throws IOException {
		
		LOGGER.info("Writing region file " + regionFile.getName());
		
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

	/**
	 * Write the entities from the appropriate chunks in this world into a
	 * <code>.mca</code> found within the <code>entities</code> folder.
	 * 
	 * @param entityFile  File to write to. Will replace if already exists.
	 * @param xOffset     X coordinate of the region file.
	 * @param xOffset     Z coordinate of the region file.
	 * @param dataVersion Data version of Minecraft version to save to.
	 * @throws IOException If an IO Exception occurs.
	 */
	public void writeEntityFile(File entityFile, int xOffset, int zOffset, int dataVersion) throws IOException {
		LOGGER.info("Writing entity file: " + entityFile.getName());
		ChunkParser parser = new ChunkParser(dataVersion);

		// Keep track of all the chunks that belong in this file.
		Map<ChunkCoordinate, CompoundTag> entityChunks = new HashMap<>();
		for (ChunkCoordinate chunkCoord : this.chunks.keySet()) {
			int relativeX = chunkCoord.x - xOffset * 32;
			int relativeZ = chunkCoord.z - zOffset * 32;

			if (0 <= relativeX && relativeX < 32 && 0 <= relativeZ && relativeZ < 32) {
				// Serialize chunk to NBT.
				entityChunks.put(chunkCoord, parser.writeEntities(this.chunks.get(chunkCoord), chunkCoord.x, chunkCoord.z));
			}
		}
		// Don't write file if there are no chunks.
		if (entityChunks.size() < 1) {
			return;
		}
		if (entityFile.exists()) {
			entityFile.delete();
		}
		
		WorldOutputStream wos = new WorldOutputStream(new FileOutputStream(entityFile), new ChunkCoordinate(xOffset, zOffset));
		
		for (ChunkCoordinate coord : entityChunks.keySet()) {
			// Convert the coordinates into region space.
			int relativeX = coord.x - xOffset*32;
			int relativeZ = coord.z - zOffset*32;
			
			wos.write(new ChunkCoordinate(relativeX, relativeZ), entityChunks.get(coord));
		}
		
		wos.close();
	}

	@Override
	public Iterator<Vector3ic> iterator() {
		throw new UnsupportedOperationException("Iterating over entire block world hasn't been implemented yet.");
	}

	@Override
	public int getSectionWidth() {
		return Chunk.WIDTH;
	}

	@Override
	public int getSectionLength() {
		return Chunk.LENGTH;
	}

	@Override
	public int getSectionHeight() {
		return Section.HEIGHT;
	}

	@Override
	public Set<Vector3ic> getSections() {
		Set<Vector3ic> sections = new HashSet<>();
		for (ChunkCoordinate c : chunks.keySet()) {
			for (int i = 0; i < Chunk.HEIGHT / Section.HEIGHT; i++) {
				sections.add(new SectionCoordinate(c, i));
			}
		}
		
		return sections;
	}
}
