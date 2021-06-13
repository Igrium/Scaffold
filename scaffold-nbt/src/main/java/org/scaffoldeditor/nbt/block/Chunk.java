package org.scaffoldeditor.nbt.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.math.Vector3i;

import net.querz.nbt.tag.CompoundTag;

/**
 * Represents a single chunk in the world.
 *
 */
public class Chunk implements SizedBlockCollection {
	
	public static final int WIDTH = 16;
	public static final int LENGTH = 16;
	public static final int HEIGHT = 256;
	
	public static class SectionCoordinate extends Vector3i {
		
		public SectionCoordinate(int x, int y, int z) {
			super(x, y, z);
		}
		
		public SectionCoordinate(ChunkCoordinate chunk, int index) {
			super(chunk.x, index, chunk.z);
		}
		
		public SectionCoordinate(Vector3i vec) {
			super(vec.x, vec.y, vec.z);
		}
		
		public ChunkCoordinate getChunk() {
			return new ChunkCoordinate(x, z);
		}
		
		/**
		 * Get the global starting X of the section.
		 */
		public int getStartX() {
			return x * WIDTH;
		}
		
		/**
		 * Get the global starting Y of the section.
		 */
		public int getStartY() {
			return y * Section.HEIGHT;
		}
		
		/**
		 * Get the global starting Z of the section.
		 */
		public int getStartZ() {
			return z * LENGTH;
		}
		
		/**
		 * Get the global ending X of the section, non-inclusive.
		 */
		public int getEndX() {
			return getStartX() + WIDTH;
		}
		
		/**
		 * Get the global ending Y of the section, non-inclusive.
		 */
		public int getEndY() {
			return getStartY() + Section.HEIGHT ;
		}
		
		/**
		 * Get the global ending Z of the section, non-inclusive.
		 */
		public int getEndZ() {
			return getStartZ() + LENGTH;
		}
		
		/**
		 * Get a coordinate in local space relative to this section in global space.
		 * @param in Local space coordinate.
		 * @return Global space coordinate.
		 * @see #relativize
		 */
		public Vector3i resolve(Vector3i in) {
			return new Vector3i(in.x + getStartX(), in.y + getStartY(), in.z + getStartZ());
		}
		
		/**
		 * Get a coordinate in global space relative to this section in local space.
		 * @param in Global space coordinate.
		 * @return Local space coordinate.
		 * @see #resolve
		 */
		public Vector3i relativize(Vector3i in) {
			return new Vector3i(in.x - getStartX(), in.y - getStartY(), in.z - getStartZ());
		}
		
		@Override
		public String toString() {
			return "SectionCoordinate: ["+x+", "+y+", "+z+"]";
		}
		
	}

	/**
	 * A list of entities in the chunk, in <a href="https://minecraft.gamepedia.com/Chunk_format#entity_format">NBT format</a>.
	 * <br>
	 * Does not do anything natively. Functionallity must be implemented externally.
	 */
	public final Collection<CompoundTag> entities = new ArrayList<>();
	
	/**
	 * A map of tile entities in the chunk, in <a href="https://minecraft.gamepedia.com/Chunk_format#entity_format">NBT format</a>.
	 * <br>
	 * Does not do anything natively. Functionallity must be implemented externally.
	 */
	public final Map<Vector3i, CompoundTag> blockEntities = new HashMap<>();
	
	@Override
	public Set<Vector3i> getBlockEntities() {
		return blockEntities.keySet();
	}
	
	@Override
	public CompoundTag blockEntityAt(Vector3i vec) {
		return blockEntities.get(vec);
	}
	
	@Override
	public Collection<CompoundTag> getEntities() {
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
		
		for (Vector3i coord : getBlockEntities()) {
			if (min <= coord.y && max > coord.y) blockEntities.remove(coord);
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
