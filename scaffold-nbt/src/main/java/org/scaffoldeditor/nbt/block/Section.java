package org.scaffoldeditor.nbt.block;

import java.util.ArrayList;
import java.util.List;

import org.scaffoldeditor.nbt.math.Vector3i;

/**
 * Represents a subchunk
 * @author Igrium
 */
public class Section implements SizedBlockCollection {
	
	public static final int HEIGHT = 16;
	
	/**
	 * A list of all the block types that are in the section.
	 */
	private List<Block> palette = new ArrayList<Block>();
	
	/**
	 * A list of all of the block owners in the section.
	 */
	private List<Object> ownerPalette = new ArrayList<>();
	
	/**
	 * All blocks in the chunk, listed by their palette index.
	 */
	private short[][][] blocks;
	
	/**
	 * All the block owners, listed by their palette index.
	 */
	private byte[][][] owners;
	
	public Section() {
		blocks = new short[Chunk.WIDTH][HEIGHT][Chunk.LENGTH];
		owners = new byte[Chunk.WIDTH][HEIGHT][Chunk.LENGTH];
		for (int y = 0; y < HEIGHT; y++) {
			for (int z = 0; z < Chunk.LENGTH; z++) {
				for (int x = 0; x < Chunk.WIDTH; x++) {
					blocks[x][y][z] = -1;
					owners[x][y][z] = -1;
				}
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
	
	@Override
	public boolean hasBlock(int x, int y, int z) {
		return blocks[x][y][z] >= 0;
	}
	
	/**
	 * Get the owner of a block in this section.
	 * @return The owner, or null if there is no owner.
	 */
	public Object getOwner(int x, int y, int z) {
		try {
			byte paletteIndex = owners[x][y][z];
			if (paletteIndex == -1) {
				return null;
			} else {
				return ownerPalette.get(paletteIndex);
			}
			
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Block "+x+" "+y+" "+z+" is out of range!");
			return null;
		}
	}
	
	/**
	 * Check for a non-air block at the given section coordinates. (More efficiant than blockAt).
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate
	 * @return Is a block present?
	 */
	public boolean blockExists(int x, int y, int z) {
		return (blocks[x][y][z] > 0);
	}
	
	public void setBlock(int x, int y, int z, Block block) {
		this.setBlock(x, y, z, block, null);
	}
	
	public void setBlock(int x, int y, int z, Block block, Object owner) {
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
		
		if (owner != null) {
			setOwner(x, y, z, owner);
		}
	}
	
	public void setOwner(int x, int y, int z, Object owner) {
		if (!ownerPalette.contains(owner)) {
			ownerPalette.add(owner); // Make sure block is in palette
		}
		
		// Find block in palette
		byte paletteIndex = 0;
		for (byte i = 0; i < ownerPalette.size(); i++) {
			if (ownerPalette.get(i).equals(owner)) {
				paletteIndex = i;
				break;
			}
		}
		
		owners[x][y][z] = paletteIndex;
	}
	
	public Block[][][] getBlocks() {
		Block[][][] blockArray = new Block[Chunk.WIDTH][HEIGHT][Chunk.LENGTH];
		
		for (int x = 0; x < Chunk.WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				for (int z = 0; z < Chunk.LENGTH	; z++) {
					blockArray[x][y][z] = palette.get(blocks[x][y][z]);
				}
			}
		}
		
		return blockArray;
	}
	
	/**
	 * Get a list of all the blocks the section has.
	 * @return Palette.
	 */
	public List<Block> palette() {
		return palette;
	}

	@Override
	public Vector3i getMin() {
		return new Vector3i(0,0,0);
	}

	@Override
	public Vector3i getMax() {
		return new Vector3i(Chunk.WIDTH, Section.HEIGHT, Chunk.LENGTH);
	}
}
