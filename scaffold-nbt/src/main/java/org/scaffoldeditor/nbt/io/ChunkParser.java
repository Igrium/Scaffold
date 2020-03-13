package org.scaffoldeditor.nbt.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.Chunk;

import mryurihi.tbnbt.TagType;
import mryurihi.tbnbt.tag.NBTTag;
import mryurihi.tbnbt.tag.NBTTagByte;
import mryurihi.tbnbt.tag.NBTTagCompound;
import mryurihi.tbnbt.tag.NBTTagInt;
import mryurihi.tbnbt.tag.NBTTagList;
import mryurihi.tbnbt.tag.NBTTagLong;
import mryurihi.tbnbt.tag.NBTTagLongArray;
import mryurihi.tbnbt.tag.NBTTagString;

/**
 * This class is responsible for parsing (and writing)
 * <a href="https://minecraft.gamepedia.com/Chunk_format"> NBT </a>
 * representing Minecraft chunks.
 */
public class ChunkParser {
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
		
		/**
		 * Create a section from a chunk and a Y offset
		 */
		public Section(Chunk chunk, byte yOffset) {
			this.y = yOffset;
			palette.add(new Block("minecraft:air")); // Air must always be the first in the palette.
			
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					for (int x = 0; x < 16; x++) {
						setBlock(x, y, z, chunk.blockAt(x, this.y*16+y, z));
					}
				}
			}
			
			valid = true;
		};
		
		private void setBlock(int x, int y, int z, Block block) {
			if (block == null) {
				return;
			}
			
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
			
			blockArray[y][z][x] = paletteIndex;
		}
		
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
		 * Generate NBT data from the section.
		 * @return NBT data.
		 */
		public NBTTagCompound getNBT() {
			if (!isValid()) {
				return null;
			}
			
			NBTTagCompound nbt = new NBTTagCompound(new HashMap<String, NBTTag>());
			nbt.put("Y", new NBTTagByte(y));
			
			// Insert palette.
			List<NBTTag> palette = new ArrayList<NBTTag>();
			for (Block b : this.palette) {
				palette.add(b.toPaletteEntry());
			}
			nbt.put("Palette", new NBTTagList(palette));
			
			// Load blockstates from 3d array.
			int[] blockStateArray = new int[4096];
			int head = 0;
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					for (int x = 0; x < 16; x++) {
						blockStateArray[head] = blockArray[y][z][x];
						head++;
					}
				}
			}
			NBTTagLongArray blockstates = new NBTTagLongArray(writeBlockStates(blockStateArray));
			nbt.put("BlockStates", blockstates);
			
			return nbt;
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
	 * <a href="https://minecraft.gamepedia.com/Data_version">Data version</a> of the chunk NBT structure to use.
	 */
	public final int dataVersion;
	
	/**
	 * Create a new chunk parser. 
	 * @param dataVersion <a href="https://minecraft.gamepedia.com/Data_version">Data version</a> of the chunk NBT structure to use.
	 */
	public ChunkParser(int dataVersion) {
		this.dataVersion = dataVersion;
	}
	
	/**
	 * Read a chunk from <a href="https://minecraft.gamepedia.com/Chunk_format">NBT data</a>
	 * @param nbt NBT to parse.
	 * @return Parsed chunk.
	 */
	public static Chunk parseNBT(NBTTagCompound nbt) {
		Chunk chunk = new Chunk();
		
		NBTTagCompound level = nbt.get("Level").getAsTagCompound();
		
//		System.out.println(nbt);
		// Get sections from NBT.
		NBTTagList sectionList = level.get("Sections").getAsTagList();
		
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
		
		// Load entities.
		if (nbt.containsKey("Entities")) {
			NBTTagList entities = nbt.get("Entities").getAsTagList();
			for (NBTTag t : entities.getValue()) {
				chunk.entities.add(t.getAsTagCompound());
			}
		}
		
		
		return chunk;
	}
	
	/**
	 * Write <a href="https://minecraft.gamepedia.com/Chunk_format">NBT data</a> for a chunk.
	 * @param chunk Chunk to write data for.
	 * @param x X coordinate of the chunk.
	 * @param z Z coordinate of the chunk.
	 * @return Written NBT.
	 */
	public NBTTagCompound writeNBT(Chunk chunk, int x, int z) {
		NBTTagCompound level = new NBTTagCompound(new HashMap<String, NBTTag>());
		
		level.put("xPos", new NBTTagInt(x));
		level.put("zPos", new NBTTagInt(z));
		
		// Write sections.
		List<NBTTag> sections = new ArrayList<NBTTag>();
		for (byte y = 0; y < Chunk.HEIGHT/16; y++) {
			if (sectionHasBlocks(chunk, y)) {
				Section section = new Section(chunk, y);
				sections.add(section.getNBT());
			}
		}
		level.put("Sections", new NBTTagList(sections));
		
		// Write entities.
		List<NBTTag> entities = new ArrayList<NBTTag>();
		for (NBTTagCompound e : chunk.entities) {
			entities.add(e);
		}
		
		if (!entities.isEmpty()) {
			level.put("Entities", new NBTTagList(entities));
		} else {
			level.put("Entities", new NBTTagList(TagType.COMPOUND));
		}
		
		// Write tile entities.
		List<NBTTag> tileEntities = new ArrayList<NBTTag>();
		for (NBTTagCompound e : chunk.tileEntities) {
			tileEntities.add(e);
		}
		
		if (!tileEntities.isEmpty()) {
			level.put("Entities", new NBTTagList(tileEntities));
		} else {
			level.put("Entities", new NBTTagList(TagType.COMPOUND));
		}
		
		// Write other shit that Minecraft needs to read the file.
		NBTTagCompound structures = new NBTTagCompound(new HashMap<String, NBTTag>());
		structures.put("References", new NBTTagCompound(new HashMap<String, NBTTag>()));
		structures.put("Starts",  new NBTTagCompound(new HashMap<String, NBTTag>()));
		level.put("Structures", structures);
		
		NBTTagList postProcessing = new NBTTagList(TagType.LIST);
		for (int i = 0; i < 16; i++) {
			postProcessing.add(new NBTTagList(TagType.COMPOUND));
		}
		level.put("PostProcessing", postProcessing);
		
		level.put("LiquidTicks", new NBTTagList(TagType.COMPOUND));
		level.put("TileTicks", new NBTTagList(TagType.COMPOUND));
		level.put("InhabitedTime", new NBTTagLong(0));
		level.put("LastUpdate", new NBTTagLong(0));
		level.put("IsLightOn", new NBTTagByte((byte) 0));
		level.put("Status", new NBTTagString("finalized"));
		
		// Finalize with data version.
		NBTTagCompound root = new NBTTagCompound(new HashMap<String, NBTTag>());
		root.put("Level", level);
		root.put("DataVersion", new NBTTagInt(dataVersion));
		return root;
	}
	
	/**
	 * Check if a section has any non-air blocks in it.
	 * @param chunk Chunk to look in.
	 * @param section Section to check.
	 * @return Were any blocks found?
	 */
	private static boolean sectionHasBlocks(Chunk chunk, int section) {
		int yOffset = section * 16;
		
		for (int y = 0; y < 16; y++) {
			for (int z = 0; z < 16; z++) {
				for (int x = 0; x < 16; x++) {
					if (chunk.blockExists(x, y+yOffset, z)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Obtain a list of BlockState indices from the BlockState long array.
	 * @param longArray Long array to parse.
	 * @return Indices of BlockStates in the palette.
	 */
	public static int[] readBlockStates(long[] longArray) {
		
		/*
		 * The size of an index in bits.
		 * One section always stores 16*16*16 = 4096 blocks,
		 * therefore the amount of bits per block can be calculated like that: 
		 * size of BlockStates-Tag * 64 / 4096 (64 = bit of a long value),
		 * which simplifies to longArrayLength/64. 
		 */
		int indexSize = longArray.length / 64;
		if (indexSize < 4) {
			indexSize = 4;
		}
				
		// Obtain all bits from long array.
		BitSet bits = BitSet.valueOf(longArray);
		
		// Convert into int array.
		int[] indices = new int[4096];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = bitsetToInt(bits.get(i*indexSize, (i+1)*indexSize-1));
		}
//		System.out.println("Indices: "+Arrays.toString(indices)); // TESTING ONLY
		return indices;
	}
	
	/**
	 * Write a BlockState long array from a list of indices.
	 * @param indices Indices to write.
	 * @return BlockState long array.
	 */
	public static long[] writeBlockStates(int[] indices) {
		// Calculate the number of bits required to store the individual indices
		// doing the integer bit size formula, ceil(logbase2(n)) on the largest index.
		int max = indices[0];
		for (int index : indices) {
			if (index > max) {
				max = index;
			}
		}
		
		/*
		 * The size of an index in bits.
		 * One section always stores 16*16*16 = 4096 blocks,
		 * therefore the amount of bits per block can be calculated like that: 
		 * size of BlockStates-Tag * 64 / 4096 (64 = bit of a long value),
		 * which simplifies to longArrayLength/64. 
		 */
		int indexSize = (int) Math.ceil(Math.log(max)/Math.log(2));
		if (indexSize < 4) {
			indexSize = 4;
		}
		// Add all bits of indices to BitSet.
		BitSet bits = BitSet.valueOf(new long[64*indexSize]);
		for (int i = 0; i < indices.length; i++) {
			insertBitSet(bits, intToBitset(indices[i]), i * indexSize);
		}
//		System.out.println(Arrays.toString(bits.toLongArray()));
		
		// BitSet.toLongArray() removes the trailing 0s. We can't have that, so we must re-add them.
		
		/*
		 * Calculate the total number of longs we will need.
		 * Given that the number of bits that need to be stored = indexSize * 4096
		 * (there are 4096 total blocks), we can plug that into the equation for the amount
		 * of longs specified by the BitSet.toLongArray() documentation.
		 */
		int numLongs = (indexSize * indices.length + 63) / 64;
		
		return Arrays.copyOf(bits.toLongArray(), numLongs);
	}
	
	/**
	 * Attempt to insert set1 into set2, starting at the o specified.
	 */
	private static void insertBitSet(BitSet set1, BitSet set2, int offset) {
		int i = 0;
		while (i < set2.length()) {
			set1.set(i+offset, set2.get(i));
			i++;
		}
	}
	
	/*
	 * Convert a BitSet to an int.
	 * Copied from: https://stackoverflow.com/questions/2473597/bitset-to-and-from-integer-long
	 */
	private static int bitsetToInt(BitSet bits) {
		int value = 0;
		for (int i = 0; i < bits.length(); ++i) {
			value += bits.get(i) ? (1L << i) : 0L;
		}
		return value;
	}
	
	/*
	 * Convert an int to a BitSet.
	 * Copied from: https://stackoverflow.com/questions/2473597/bitset-to-and-from-integer-long
	 */
	public static BitSet intToBitset(int value) {
		BitSet bits = new BitSet();
		int index = 0;
		while (value != 0L) {
			if (value % 2L != 0) {
				bits.set(index);
			}
			++index;
			value = value >>> 1;
		}
		return bits;
	}
}
