package org.scaffoldeditor.nbt.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.Chunk;
import mryurihi.tbnbt.tag.NBTTag;
import mryurihi.tbnbt.tag.NBTTagCompound;
import mryurihi.tbnbt.tag.NBTTagList;
import mryurihi.tbnbt.tag.NBTTagLongArray;

/**
 * This class is responsible for parsing (and writing)
 * <a href="https://minecraft.gamepedia.com/Chunk_format"> NBT </a>
 * representing Minecraft chunks.
 */
public final class ChunkParser {
	/**
	 * Represents a single sub-chunk.
	 * <br>
	 * Used for serialization/unserialization
	 * @author Sam54123
	 */
	protected static class Section {
		
		public List<Block> palette = new ArrayList<Block>();
		int[][][] blockArray = new int[16][16][16]; // Sections are 16 x 16 x 16 blocks. Stored in YZX order.
		public byte y = 0;
		
		// Variable to keep track of if section creation failed.
		private boolean valid = false;
		
		public Section() {};
		
		/**
		 * Create a Section from NBT data.
		 */
		public Section(NBTTagCompound nbt) {
			if (!nbt.containsKey("Palette")) {
				return;
			}
			this.y = nbt.get("Y").getAsTagByte().getValue();
			
			// Load palette
			NBTTagList palette = nbt.get("Palette").getAsTagList();
			for (NBTTag t : palette.getValue()) {
				NBTTagCompound block = t.getAsTagCompound();
				this.palette.add(Block.fromBlockPalleteEntry(block));
			}
			
			// Load blockstates
			NBTTagLongArray blockstates = nbt.get("BlockStates").getAsTagLongArray();
			int[] blockStateArray = readBlockStates(blockstates.getValue());
			
			// Insert all blockstates indices into 3d array.
			int head = 0;
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					for (int x = 0; x < 16; x++) {
						blockArray[y][z][x] = blockStateArray[head];
						head++;
					}
				}
			}
			
			valid = true;
		}
		
		/**
		 * Was this section initialized properly?
		 * @return Is valid?
		 */
		public boolean isValid() {
			return valid;
		}

		public Block blockAt(int x, int y, int z) {
			if (valid) {
				return palette.get(blockArray[y][z][x]);
			} else {
				return null;
			}
			
		}

	}
	
	/**
	 * Read a chunk from <a href="https://minecraft.gamepedia.com/Chunk_format">NBT data</a>
	 * @param nbt NBT to parse.
	 * @return Parsed chunk.
	 */
	public static Chunk fromNBT(NBTTagCompound nbt) {
		Chunk chunk = new Chunk();
		
		// Get sections from NBT.
		NBTTagList sectionList = nbt.get("Sections").getAsTagList();
		
		// Iterate through sections.
		for (NBTTag t : sectionList.getValue()) {
			NBTTagCompound subchunk = t.getAsTagCompound();
			
			Section section = new Section(subchunk);
			int yOffset = section.y*16;
			
			// Add section to chunk.
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					for (int x = 0; x < 16; x++) {
						Block block = section.blockAt(x, y, z);
						if (block != null) {
							chunk.setBlock(x, y+yOffset, z, block);
						}			
					}
				}
			}
		}
			
		return chunk;
	}
	
	/**
	 * Obtain a list of BlockState indices from the BlockState long array.
	 * @param longArray Long array to parse.
	 * @return Indices of BlockStates in the palette.
	 */
	private static int[] readBlockStates(long[] longArray) {
		
		/*
		 * The size of an index in bits.
		 * One section always stores 16*16*16 = 4096 blocks,
		 * therefore the amount of bits per block can be calculated like that: 
		 * size of BlockStates-Tahttps://www.youtube.com/watch?v=CKR_1vh5Jw0g * 64 / 4096 (64 = bit of a long value). 
		 */
		int indexSize = longArray.length * 64 / 4096;
		if (indexSize < 4) {
			indexSize = 4;
		}
				
		// Obtain all bits from long array.
		BitSet bits = BitSet.valueOf(longArray);
		
		// Convert into int array.
		int[] indices = new int[4096];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = convert(bits.get(i*indexSize, (i+1)*indexSize-1));
		}
						
		return indices;
	}
	
	/*
	 * Convert a BitSet to a Long.
	 * Copied from: https://stackoverflow.com/questions/2473597/bitset-to-and-from-integer-long
	 */
	private static int convert(BitSet bits) {
	    int value = 0;
	    for (int i = 0; i < bits.length(); ++i) {
	      value += bits.get(i) ? (1L << i) : 0L;
	    }
	    return value;
	  }
}
