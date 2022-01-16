package org.scaffoldeditor.nbt.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import net.querz.nbt.tag.CompoundTag;

/**
 * Represents a single chunk in the world.
 *
 */
public class Chunk implements SizedBlockCollection {
	
	public static final int WIDTH = 16;
	public static final int LENGTH = 16;
	public static final int HEIGHT = 256;
	

	/**
	 * All of the entities in the chunk in pairs where the first item is the entity
	 * in <a href="https://minecraft.gamepedia.com/Chunk_format#entity_format">NBT
	 * format</a>, and the second item is the entity's position. <br>
	 * Does not do anything natively. Functionallity must be implemented externally.
	 */
	public final Map<CompoundTag, Vector3dc> entities = new HashMap<>();
	
	/**
	 * A map of tile entities in the chunk, in <a href="https://minecraft.gamepedia.com/Chunk_format#entity_format">NBT format</a>.
	 * <br>
	 * Does not do anything natively. Functionallity must be implemented externally.
	 */
	public final Map<Vector3ic, CompoundTag> blockEntities = new HashMap<>();
	
	@Override
	public Set<Vector3ic> getBlockEntities() {
		return blockEntities.keySet();
	}
	
	@Override
	public CompoundTag blockEntityAt(Vector3ic vec) {
		return blockEntities.get(vec);
	}
	
	public Map<CompoundTag, Vector3dc> getEntities() {
		return entities;
	}
	
	/**
	 * All the sections in the chunk.
	 */
	public final Section[] sections = new Section[HEIGHT / Section.HEIGHT];
	
	public Chunk() {
		for (int i = 0; i < sections.length; i++) {
			sections[i] = new Section();
		}
	}
	
	@Override
	public Block blockAt(int x, int y, int z) {
		Section section = sections[Math.floorDiv(y, Section.HEIGHT)];
		return section.blockAt(x, y % Section.HEIGHT, z);
	}
	
	@Override
	public boolean hasBlock(int x, int y, int z) {
		Section section = sections[Math.floorDiv(y, Section.HEIGHT)];
		return section.hasBlock(x, y & Section.HEIGHT, z);
	}
	
	/**
	 * Get the owner of a block in this chunk.
	 * @return The owner, or null if there is no owner.
	 */
	public Object getOwner(int x, int y, int z) {
		Section section = sections[Math.floorDiv(y, Section.HEIGHT)];
		return section.getOwner(x, y % Section.HEIGHT, z);
	}
	
	public void clearSection(int index) {
		if (index < 0 || index > sections.length) {
			throw new IllegalArgumentException("Section index not within range!");
		}
		int min = index * Section.HEIGHT;
		int max = min + Section.HEIGHT;
		
		for (Vector3ic coord : getBlockEntities()) {
			if (min <= coord.y() && max > coord.y()) blockEntities.remove(coord);
		}
		
		sections[index] = null;
	}
	
	/**
	 * Check for a non-air block at the given chunk coordinates. (More efficiant than blockAt).
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate
	 * @return Is a block present?
	 */
	public boolean blockExists(int x, int y, int z) {
		Section section = sections[Math.floorDiv(y, Section.HEIGHT)];
		return section.blockExists(x, y % Section.HEIGHT, z);
	}
	
	public void setBlock(int x, int y, int z, Block block) {
		setBlock(x, y, z, block, null);
	}

	public void setBlock(int x, int y, int z, Block block, Object owner) {
		Section section = sections[Math.floorDiv(y, Section.HEIGHT)];
		section.setBlock(x, y % Section.HEIGHT, z, block, owner);
	}
	
	public void setOwner(int x, int y, int z, Object owner) {
		Section section = sections[Math.floorDiv(y, Section.HEIGHT)];
		section.setOwner(x, y % Section.HEIGHT, z, owner);
	}

	@Override
	public Vector3i getMin() {
		return new Vector3i(0, 0, 0);
	}

	@Override
	public Vector3i getMax() {
		return new Vector3i(Chunk.WIDTH, Chunk.HEIGHT, Chunk.LENGTH);
	}
}
