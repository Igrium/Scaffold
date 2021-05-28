package org.scaffoldeditor.nbt.schematic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;

import net.querz.nbt.tag.CompoundTag;

public class Construction implements SizedBlockCollection {

	public static class Section implements SizedBlockCollection {
		
		public final int width;
		public final int length;	
		public final int height;
		public final int[][][] blocks;
		public final List<Block> palette;
		
		public Section(int width, int height, int length, int[][][] blocks, List<Block> palette) {
			if (width < 0 || height < 0 || length < 0) {
				throw new IllegalArgumentException("Width, height, and length of section must be positive!");
			}
			
			this.width = width;
			this.length = length;
			this.height = height;
			this.blocks = blocks;
			this.palette = palette;
		}
		
		public final List<CompoundTag> entities = new ArrayList<>();
		public final List<CompoundTag> blockEntities = new ArrayList<>();

		@Override
		public Block blockAt(int x, int y, int z) {
			if (x >= width || y >= height || z >= length) return null;
			int index = blocks[x][y][z];
			return palette.get(index);
		}


		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public int getLength() {
			return length;
		}
		
	}
	
	public final List<Block> palette = new ArrayList<>();
	public final Map<SectionCoordinate, Section> sections = new HashMap<>();


	@Override
	public Block blockAt(int x, int y, int z) {
		System.out.println(x+", "+y+", "+z);
		SectionCoordinate section = sectionAt(x, y, z);
		int localX = x - section.getStartX();
		int localY = y - section.getStartY();
		int localZ = z - section.getStartZ();
		return sections.get(section).blockAt(localX, localY, localZ);
	}
	
	public SectionCoordinate sectionAt(int x, int y, int z) {
		return new SectionCoordinate(Math.floorDiv(x, 16), Math.floorDiv(y, 16), Math.floorDiv(z, 16));
	}

	@Override
	public int getWidth() {
		int highest = 0;
		for (SectionCoordinate coord : sections.keySet()) {
			if (coord.x > highest) highest = coord.x;
		}
		return highest * 16;
	}

	@Override
	public int getHeight() {
		int highest = 0;
		for (SectionCoordinate coord : sections.keySet()) {
			if (coord.y > highest) highest = coord.y;
		}
		return highest * 16;
	}

	@Override
	public int getLength() {
		int highest = 0;
		for (SectionCoordinate coord : sections.keySet()) {
			if (coord.z > highest) highest = coord.z;
		}
		return highest * 16;
	}

}
