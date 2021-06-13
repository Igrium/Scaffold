package org.scaffoldeditor.nbt.schematic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.ChunkedBlockCollection;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.math.Vector3d;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.nbt.util.Pair;

import net.querz.nbt.tag.CompoundTag;

/**
 * Represents a block collection in the 
 * <a href="https://github.com/Amulet-Team/construction-specification">Construction format</a>
 * @author Igrium
 */
public class Construction implements ChunkedBlockCollection {
	
	/**
	 * A 16x16x16 section within the Construction format. Although sections in Construction can take up only
	 * a section of the 16m³ voxel, this class abstracts all of that away. You only need to worry about block
	 * coordinates relative to the on-grid root.
	 * @author Igrium
	 */
	public static class Section implements SizedBlockCollection {
		
		public final int width;
		public final int length;	
		public final int height;
		public final int[][][] blocks;
		public final List<Block> palette;
		public final int[] relativeStartCoords;
		
		public Section(int width, int height, int length, int[][][] blocks, List<Block> palette, int relativeStartCoords[]) {
			if (width < 0 || height < 0 || length < 0) {
				throw new IllegalArgumentException("Width, height, and length of section must be positive!");
			}
						
			this.width = width;
			this.length = length;
			this.height = height;
			this.blocks = blocks;
			this.palette = palette;
			this.relativeStartCoords = relativeStartCoords;
		}
		
		public final List<Pair<CompoundTag, Vector3d>> entities = new ArrayList<>();
		public final Map<Vector3i, CompoundTag> blockEntities = new HashMap<>();

		@Override
		public Block blockAt(int x, int y, int z) {
			int startX = relativeStartCoords[0];
			int startY = relativeStartCoords[1];
			int startZ = relativeStartCoords[2];
			if (x >= width + startX || y >= height + startY || z >= length + startZ || x < startX || y < startY || z < startZ) return null;
			int index = blocks[x - startX][y - startY][z - startZ];
			return palette.get(index);
		}

		@Override
		public Vector3i getMin() {
			return new Vector3i(relativeStartCoords[0], relativeStartCoords[1], relativeStartCoords[2]);
		}

		@Override
		public Vector3i getMax() {
			return new Vector3i(relativeStartCoords[0] + width, relativeStartCoords[1] + height, relativeStartCoords[2] + length);
		}
		
		@Override
		public Set<Vector3i> getBlockEntities() {
			return blockEntities.keySet();
		}
		
		@Override
		public CompoundTag blockEntityAt(Vector3i vec) {
			return blockEntities.get(vec);
		}
		
		@Override
		public Collection<Pair<CompoundTag, Vector3d>> getEntities() {
			return entities;
		}
	}
	
	/**
	 * Stores the coordinates of the areas that were selected in creating a construction file.
	 * @author Igrium
	 */
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
	
	/**
	 * <p>
	 * Like Minecraft worlds, Construction files are inherantly unlimited in size.
	 * This wrapper class allows you to use a segment of a construction in functions
	 * requiring a {@link SizedBlockCollection}.
	 * </p>
	 * <p>
	 * Note: segments do not keep their own copy of blocks. Instead, they reference
	 * the parent Construction and obtain blocks relative to their root position.
	 * Width, height, and length are only used to satisfy the requirements of
	 * SizedBlockCollection.
	 * </p>
	 * <p>
	 * This should be instantiated from
	 * {@link Construction#getSegment(SelectionBox)}
	 * </p>
	 * 
	 * @author Igrium
	 */
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
		public Vector3i getMin() {
			return new Vector3i(0, 0, 0);
		}

		@Override
		public Vector3i getMax() {
			return new Vector3i(width, height, length);
		}
		
		@Override
		public CompoundTag blockEntityAt(Vector3i vec) {
			return Construction.this.blockEntityAt(new Vector3i(vec.x + rootX, vec.y + rootY, vec.z + rootZ));
		}
		
		@Override
		public Set<Vector3i> getBlockEntities() {
			Set<Vector3i> ents = new HashSet<>();
			for (SectionCoordinate secCoord : getOverlappingSections()) {
				Section section = sections.get(secCoord);
				if (section != null) {
					for (Vector3i coord : section.getBlockEntities()) {
						Vector3i globalCoord = secCoord.resolve(coord);
						ents.add(globalCoord.subtract(new Vector3i(rootX, rootY, rootZ)));
					}
				}
			}
			
			return ents;
		}
		
		@Override
		public Collection<Pair<CompoundTag, Vector3d>> getEntities() {
			List<Pair<CompoundTag, Vector3d>> ents = new ArrayList<>();
			for (SectionCoordinate secCoord : getOverlappingSections()) {
				Section section = sections.get(secCoord);
				if (section != null) {
					for (Pair<CompoundTag, Vector3d> ent : section.getEntities()) {
						Vector3d globalCoord = ent.getSecond().add(secCoord.getStartPos().toDouble());
						ents.add(new Pair<>(ent.getFirst(), globalCoord.subtract(new Vector3d(rootX, rootY, rootZ))));
					}
				}
			}
			
			return ents;
		}
		
		protected Set<SectionCoordinate> getOverlappingSections() {
			Set<SectionCoordinate> overlapping = new HashSet<>();
			Vector3i min = new Vector3d(rootX, rootY, rootZ).divide(16).floor();
			Vector3i max = new Vector3d(rootX + width, rootY + height, rootZ + length).divide(16).floor();
			
			for (int x = min.x; x <= max.x; x++) {
				for (int y = min.y; y <= max.y; y++) {
					for (int z = min.z; z <= max.z; z++) {
						overlapping.add(new SectionCoordinate(x, y, z));
					}
				}
			}
			return overlapping;
		}	
	}
	
	public final List<Block> palette = new ArrayList<>();
	public final Map<SectionCoordinate, Section> sections = new HashMap<>();
	public final List<SelectionBox> selectionBoxes = new ArrayList<>();

	/**
	 * Obtain a {@link ConstructionSegment} from this Construction.
	 * @param box Selection box to create the segment from.
	 * @return A ConstructionSegment of that selection.
	 */
	public ConstructionSegment getSegment(SelectionBox box) {
		return new ConstructionSegment(box);
	}

	/**
	 * Get the block at a location within the Construction.
	 * <br> Note: By default, Construction uses the global coordinates
	 * at which the schematic was built in Minecraft. Use {@link ConstructionSegment} to
	 * circumvent this.
	 */
	public Block blockAt(int x, int y, int z) {
		SectionCoordinate section = sectionCoordAt(x, y, z);
		if (!sections.containsKey(section)) return null;
		int localX = x - section.getStartX();
		int localY = y - section.getStartY();
		int localZ = z - section.getStartZ();
		
		return sections.get(section).blockAt(localX, localY, localZ);
	}
	
	public SectionCoordinate sectionCoordAt(int x, int y, int z) {
		return new SectionCoordinate(Math.floorDiv(x, 16), Math.floorDiv(y, 16), Math.floorDiv(z, 16));
	}

	@Override
	public Iterator<Vector3i> iterator() {
		return null;
	}

	@Override
	public int getSectionWidth() {
		return 16;
	}

	@Override
	public int getSectionLength() {
		return 16;
	}

	@Override
	public int getSectionHeight() {
		return 16;
	}
	
	@Override
	public Set<Vector3i> getBlockEntities() {
		Set<Vector3i> locations = new HashSet<>();
		for (SectionCoordinate secCoord : sections.keySet()) {
			Section section = sections.get(secCoord);
			if (section != null) {
				for (Vector3i entCoord : section.blockEntities.keySet()) {
					locations.add(secCoord.resolve(entCoord));
				}
			}
		}
		
		return locations;
	}
	
	@Override
	public CompoundTag blockEntityAt(Vector3i vec) {
		SectionCoordinate secCoord = sectionCoordAt(vec.x, vec.y, vec.z);
		Section section = sections.get(secCoord);
		if (section == null) return null;
		return section.blockEntityAt(secCoord.relativize(vec));
	}
	
	@Override
	public Collection<Pair<CompoundTag, Vector3d>> getEntities() {
		List<Pair<CompoundTag, Vector3d>> entities = new ArrayList<>();
		
		for (SectionCoordinate secCoord : sections.keySet()) {
			for (Pair<CompoundTag, Vector3d> ent : sections.get(secCoord).getEntities()) {
				entities.add(new Pair<>(ent.getFirst(), ent.getSecond().add(secCoord.getStartPos().toDouble())));
			}
		}
		
		return entities;
	}

	@Override
	public Section sectionAt(int x, int y, int z) {
		return sections.get(new SectionCoordinate(x, y, z));
	}

	@Override
	public Set<Vector3i> getSections() {
		Set<Vector3i> set = new HashSet<>();
		for (SectionCoordinate c : sections.keySet()) {
			set.add(c);
		}
		return set;
	}
}
