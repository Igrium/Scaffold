package org.scaffoldeditor.nbt.io;

import java.util.ArrayList;
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
	 * Used for serialization/unserialization
	 * @author Sam54123
	 */
	protected static class Section {
		
		public List<Block> palette = new ArrayList<Block>();
		Block[][][] blockArray = new Block[16][16][16]; // Sections are 16 x 16 x 16 blocks.
		public byte y = 0;
		
		public Section() {};
		
		/**
		 * Create a SubChunk from NBT data.
		 */
		public Section(NBTTagCompound nbt) {
			if (!nbt.containsKey("Palette")) {
				return;
			}
			System.out.println(nbt);
			this.y = nbt.get("Y").getAsTagByte().getValue();
			
			// Load palette
			NBTTagList palette = nbt.get("Palette").getAsTagList();
			for (NBTTag t : palette.getValue()) {
				NBTTagCompound block = t.getAsTagCompound();
//				System.out.println("Block: "+t);
				this.palette.add(Block.fromBlockPalleteEntry(block));
			}
			
			// Load blockstates
			NBTTagLongArray blockstates = nbt.get("BlockStates").getAsTagLongArray();
			readBlockStates(blockstates.getValue());

		}

		public Block blockAt(int x, int y, int z) {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	/**
	 * Read a chunk from <a href="https://minecraft.gamepedia.com/Chunk_format">NBT data</a>
	 * @param nbt NBT to parse.
	 * @return Parsed chunk.
	 */
	public static Chunk fromNBT(NBTTagCompound nbt) {
		NBTTagList sections = nbt.get("Sections").getAsTagList();
		
		for (NBTTag t : sections.getValue()) {
			NBTTagCompound subchunk = t.getAsTagCompound();
			
			Section section = new Section(subchunk);
		}
			
		return null;
	}
	
	/**
	 * Obtain a list of BlockState indices from the BlockState long array.
	 * @param longArray Long array to parse.
	 * @return BlockState indices.
	 */
	private static List<Integer> readBlockStates(long[] longArray) {
		
		/*
		 * The size of an index in bits.
		 * One section always stores 16*16*16 = 4096 blocks,
		 * therefore the amount of bits per block can be calculated like that: 
		 * size of BlockStates-Tag * 64 / 4096 (64 = bit of a long value). 
		 */
		int indexSize = longArray.length * 64 / 4096;
		if (indexSize < 4) {
			indexSize = 4;
		}
		
		System.out.println("Index size: "+indexSize); // TESTING ONLY
		for (long l : longArray) {
			
		}
		return null;
	}
}
