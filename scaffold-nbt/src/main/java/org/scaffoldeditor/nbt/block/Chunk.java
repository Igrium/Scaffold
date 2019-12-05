package org.scaffoldeditor.nbt.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mryurihi.tbnbt.tag.NBTTagCompound;

/**
 * Represents a single chunk in the world.
 *
 */
public class Chunk implements BlockCollection {
	
	public static final int WIDTH = 16;
	public static final int LENGTH = 16;
	public static final int HEIGHT = 256;
	
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
	public final List<NBTTagCompound> entities = new ArrayList<NBTTagCompound>();
	
	/**
	 * A list of tile entities in the chunk, in <a href="https://minecraft.gamepedia.com/Chunk_format#entity_format">NBT format</a>.
	 * <br>
	 * Does not do anything natively. Functionallity must be implemented externally.
	 */
	public final List<NBTTagCompound> tileEntities = new ArrayList<NBTTagCompound>();
	
	public Chunk() {
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
		try {
			short paletteIndex = blocks[x][y][z];
			if (paletteIndex == -1) {
				return null;
			} else {
				return palette.get(paletteIndex);
			}
			
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Block "+x+" "+y+" "+z+" is out of range!");
			return null;
		}
	}
	
	/**
	 * Check for a non-air block at the given chunk coordinates. (More efficiant than blockAt).
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate
	 * @return Is a block present?
	 */
	public boolean blockExists(int x, int y, int z) {
		return (blocks[x][y][z] > 0);
	}

	public void setBlock(int x, int y, int z, Block block) {
		if (!palette.contains(block)) {
			palette.add(block); // Make sure block is in palette
		}
		
		// Find block in palette
		short paletteIndex = 0;
		for (short i = 0; i < palette.size(); i++) {
			if (palette.get(i).equals(block)) {
				paletteIndex = i;
				break;
			}
		}
		
		blocks[x][y][z] = paletteIndex;
	}
	
	/**
	 * Get a list of all the blocks the chunk has.
	 * @return Palette.
	 */
	public List<Block> palette() {
		return palette;
	}
	
	public Block[][][] getBlocks() {
		Block[][][] blockArray = new Block[WIDTH][HEIGHT][LENGTH];
		
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				for (int z = 0; z < LENGTH; z++) {
					blockArray[x][y][z] = palette.get(blocks[x][y][z]);
				}
			}
		}
		
		return blockArray;
	}

	@Override
	public Iterator<Block> iterator() {
		return new Iterator<Block>() {
			
			private int headX = 0;
			private int headY = 0;
			private int headZ = 0;

			@Override
			public boolean hasNext() {
				// Backup the heads.
				int oldHeadX = this.headX;
				int oldHeadY = this.headY;
				int oldHeadZ = this.headZ;

				// Search for additional values
				boolean success = false;
				while (headX < WIDTH && headY < HEIGHT && headZ < LENGTH) {
					if (blocks[headX][headY][headZ] != -1) {
						success = true;
						break;
					}
					iterate();
				}

				headX = oldHeadX;
				headY = oldHeadY;
				headZ = oldHeadZ;

				return success;
			}

			@Override
			public Block next() {
				short index = -1;
				
				// Iterate until we find a non-void block
				while (index < 0 && hasNext()) {
					index = blocks[headX][headY][headZ];
					iterate();
				}
				
				iterate();
				return palette.get(index);
			}
			
			/**
			 * Move the heads to the next available slot.
			 * Scans in an X -> Z -> Y order
			 */
			private void iterate() {
				if (headX+1 < WIDTH) {
					headX++;
				} else if (headZ+1 < LENGTH) {
					headX = 0;
					headZ++;
				} else if (headY < HEIGHT) {
					headX = 0;
					headZ = 0;
					headY++;
				}
			}
		};
	}
}
