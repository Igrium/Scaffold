package org.metaversemedia.scaffold.level;

import java.util.List;

import org.metaversemedia.scaffold.math.Vector;
import org.metaversemedia.scaffold.nbt.Block;
import org.metaversemedia.scaffold.nbt.BlockCollection;

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
	private List<Block> palette;
	
	/**
	 * All blocks in the chunk, listed by their palette index.
	 */
	private short[][][] blocks;
	
	public Chunk() {
		blocks = new short[WIDTH][HEIGHT][LENGTH];
	}
	
	@Override
	public Block blockAt(int x, int y, int z) {
		try {
			return palette.get(blocks[x][y][z]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Block "+x+" "+y+" "+z+" is out of range!");
			return null;
		}
	}
	
	/**
	 * Get the block at a particular location.
	 * @param pos Location.
	 * @return Block.
	 */
	public Block blockAt(Vector pos) {
		return blockAt((int) pos.X(), (int) pos.Y(), (int) pos.Z());
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

}
