package org.scaffoldeditor.nbt.schematic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.math.Vector3i;

/**
 * Represents a generic block collection (not representing any file format) that
 * can be easily written to at runtime.
 * 
 * @author Igrium
 */
public class GenericSchematic implements SizedBlockCollection {
	
	protected short[][][] blocks;
	protected List<Block> palette = new ArrayList<>();
	
	/**
	 * Create an empty schematic.
	 * @param width X in blocks.
	 * @param height Y in blocks.
	 * @param length Z in blocks.
	 */
	public GenericSchematic(int width, int height, int length) {
		blocks = new short[width][height][length];
		
		for (short[][] square : blocks) {
			for (short[] line : square) {
				Arrays.fill(line, (short) -1);
			}
		}
	}

	@Override
	public Block blockAt(int x, int y, int z) {
		short index = blocks[x][y][z];
		if (index < 0) return null;	
		return palette.get(blocks[x][y][z]);
	}

	@Override
	public Vector3i getMin() {
		return new Vector3i(0, 0, 0);
	}

	@Override
	public Vector3i getMax() {
		return new Vector3i(blocks.length, blocks[0].length, blocks[0][0].length);
	}
	
	public void setBlock(Block block, int x, int y, int z) {
		if (block == null) {
			blocks[x][y][z] = -1;
			return;
		}
		
		// Add to palette.
		int index = palette.indexOf(block);
		if (index < 0) {
			index = palette.size();
			palette.add(block);
		}
		
		blocks[x][y][z] = (short) index;
	}
	
	public void setBlock(Block block, Vector3i pos) {
		setBlock(block, pos.x, pos.y, pos.z);
	}

}
