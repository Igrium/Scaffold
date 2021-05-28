package org.scaffoldeditor.nbt.schematic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;

import net.querz.nbt.tag.CompoundTag;

public class Construction implements BlockCollection {

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
	
	public static class SelectionBox {
		public final int minX;
		public final int minY;
		public final int minZ;
		
		public final int maxX;
		public final int maxY;
		public final int maxZ;
		
		public SelectionBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}
	}
	
	public class ConstructionSegment implements SizedBlockCollection {
		
		public final int rootX;
		public final int rootY;
		public final int rootZ;
		
		public final int width;
		public final int height;
		public final int length;
		
		public ConstructionSegment(int rootX, int rootY, int rootZ, int width, int height, int length) {
			this.rootX = rootX;
			this.rootY = rootY;
			this.rootZ = rootZ;
			
			this.width = width;
			this.height = height;
			this.length = length;
		}
		
		public ConstructionSegment(SelectionBox box) {
			rootX = box.minX;
			rootY = box.minY;
			rootZ = box.minZ;
			
			width = box.maxX - box.minX;
			height = box.maxY - box.minY;
			length = box.maxZ - box.minZ;
		}

		@Override
		public Block blockAt(int x, int y, int z) {
			return Construction.this.blockAt(x + rootX, y + rootY, z + rootZ);
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
	public final List<SelectionBox> selectionBoxes = new ArrayList<>();

	public ConstructionSegment getSegment(SelectionBox box) {
		return new ConstructionSegment(box);
	}

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


}
