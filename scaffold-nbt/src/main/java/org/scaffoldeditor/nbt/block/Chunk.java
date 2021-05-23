package org.scaffoldeditor.nbt.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;

import net.querz.nbt.tag.CompoundTag;

/**
 * Represents a single chunk in the world.
 *
 */
public class Chunk implements BlockCollection {
	
	public static final int WIDTH = 16;
	public static final int LENGTH = 16;
	public static final int HEIGHT = 256;
	
	public static class SectionCoordinate {
		/** X position of the chunk */
		public final int x;
		/** Index of the section in the chunk */
		public final int y;
		/** Z position of the chunk */
		public final int z;
		
		public SectionCoordinate(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public SectionCoordinate(ChunkCoordinate chunk, int index) {
			this.x = chunk.x();
			this.y = index;
			this.z = chunk.z();
		}
		
		public ChunkCoordinate getChunk() {
			return new ChunkCoordinate(x, z);
		}
		
		public int getStartX() {
			return x * WIDTH;
		}
		
		public int getStartY() {
			return y * Section.HEIGHT;
		}
		
		public int getStartZ() {
			return z * LENGTH;
		}
		
		public int getEndX() {
			return getStartX() + WIDTH - 1;
		}
		
		public int getEndY() {
			return getStartY() + Section.HEIGHT - 1;
		}
		
		public int getEndZ() {
			return getStartZ() + LENGTH - 1;
		}
		
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SectionCoordinate)) {
				return false;
			}
			SectionCoordinate other = (SectionCoordinate) obj;
			return (x == other.x && y == other.y && z == other.z);	
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(x, y, z);
		}
		
		@Override
		public String toString() {
			return "SectionCoordinate: ["+x+", "+y+", "+z+"]";
		}
	}
	
	/**
	 * A list of all the block types that are in the chunk.
	 */
	private List<Block> palette = new ArrayList<Block>();
	
	/**
	 * All blocks in the chunk, listed by their palette index.
	 */
	private short[][][] blocks;
	
	/**
	 * A list of entities in the chunk, in <a href="https://minecraft.gamepedia.com/Chunk_format#entity_format">NBT format</a>.
	 * <br>
	 * Does not do anything natively. Functionallity must be implemented externally.
	 */
	public final List<CompoundTag> entities = new ArrayList<>();
	
	/**
	 * A list of tile entities in the chunk, in <a href="https://minecraft.gamepedia.com/Chunk_format#entity_format">NBT format</a>.
	 * <br>
	 * Does not do anything natively. Functionallity must be implemented externally.
	 */
	public final List<CompoundTag> tileEntities = new ArrayList<>();
	
	/**
	 * All the sections in the chunk.
	 */
	public final Section[] sections = new Section[HEIGHT / Section.HEIGHT];
	
	public Chunk() {
		for (int i = 0; i < sections.length; i++) {
			sections[i] = new Section();
		}
		
		blocks = new short[WIDTH][HEIGHT][LENGTH];
		for (short[][] row : blocks) {
			for (short[] column : row) {
				Arrays.fill(column, (short) -1);
			}
		}
		
		palette.add(new Block("minecraft:air")); // 0 in the palette is always air.
	}
	
	@Override
	public Block blockAt(int x, int y, int z) {
		Section section = sections[Math.floorDiv(y, Section.HEIGHT)];
		return section.blockAt(x, y % Section.HEIGHT, z);
	}
	
	/**
	 * Get the owner of a block in this chunk.
	 * @return The owner, or null if there is no owner.
	 */
	public Object getOwner(int x, int y, int z) {
		Section section = sections[Math.floorDiv(y, Section.HEIGHT)];
		return section.getOwner(x, y % Section.HEIGHT, z);
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
//		return (blocks[x][y][z] > 0);
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
	public Iterator<Block> iterator() {
		return new Iterator<Block>() {
			
			int currentSection = 0;
			Iterator<Block> sectionIterator = sections[0].iterator();

			@Override
			public boolean hasNext() {
				return currentSection < sections.length - 1 || sectionIterator.hasNext();
			}

			@Override
			public Block next() {
				if (sectionIterator.hasNext()) {
					return sectionIterator.next();
				} else {
					currentSection++;
					sectionIterator = sections[currentSection].iterator();
					return sectionIterator.next();
				}
			}
		};
	}
}
