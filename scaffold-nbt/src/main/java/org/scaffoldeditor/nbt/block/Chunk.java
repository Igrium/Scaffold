package org.scaffoldeditor.nbt.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mryurihi.tbnbt.tag.NBTTag;
import mryurihi.tbnbt.tag.NBTTagCompound;
import mryurihi.tbnbt.tag.NBTTagList;
import mryurihi.tbnbt.tag.NBTTagLongArray;

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
	
	public Chunk() {
		blocks = new short[WIDTH][HEIGHT][LENGTH];
		for (short[][] row : blocks) {
			for (short[] column : row) {
				Arrays.fill(column, (short) -1);
			}
		}
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
	
	/**
	 * Represents a single sub-chunk.
	 * Used for serialization/unserialization
	 * @author Sam54123
	 */
	protected static class SubChunk {
		
		public List<Block> palette;
		Block[][][] blockArray = new Block[WIDTH][16][LENGTH];
		public byte y = 0;
		
		public SubChunk() {};
		
		/**
		 * Create a SubChunk from NBT data.
		 */
		public SubChunk(NBTTagCompound nbt) {
			if (!nbt.containsKey("Palette")) {
				return;
			}
			System.out.println(nbt);
			this.y = nbt.get("Y").getAsTagByte().getValue();
			// Load palette
			NBTTagList palette = nbt.get("Palette").getAsTagList();
			for (NBTTag t : palette.getValue()) {
				NBTTagCompound block = (NBTTagCompound) t;
				this.palette.add(Block.fromBlockPalleteEntry(block));
			}
			
			// Load blockstates
			NBTTagLongArray blockstates = nbt.get("BlockStates").getAsTagLongArray();
			for (long l : blockstates.getValue()) {
				System.out.println(l);
			}

		}

		public Block blockAt(int x, int y, int z) {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	/**
	 * Read a chunk from <a href="https://minecraft.gamepedia.com/Chunk_format">NBT data</a>
	 * @param nbt
	 * @return
	 */
	public static Chunk fromNBT(NBTTagCompound nbt) {
		NBTTagList sections = nbt.get("Sections").getAsTagList();
		
		for (NBTTag t : sections.getValue()) {
			NBTTagCompound section = t.getAsTagCompound();
			
			SubChunk subchunk = new SubChunk(section);
		}
			
		return null;
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
