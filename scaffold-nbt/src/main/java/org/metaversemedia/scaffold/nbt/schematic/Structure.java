package org.metaversemedia.scaffold.nbt.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.metaversemedia.scaffold.nbt.Block;
import org.metaversemedia.scaffold.nbt.BlockCollection;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.stream.NBTInputStream;

/**
 * Represents a Minecraft structure schematic (.nbt)
 * @author Sam54123
 */
public class Structure implements BlockCollection {
	
	private CompoundMap[] palette;
	private CompoundMap[] blocks;
	private List<CompoundMap> entities;
	
	private int sizeX;
	private int sizeY;
	private int sizeZ;
	
	private Structure() {};
	
	/**
	 * Get the name of the block at a particular location.
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Block blockAt(int x, int y, int z) {
		
		// Look for block with matching coords
		for (CompoundMap block : blocks) {
			ListTag<IntTag> coordTag = (ListTag<IntTag>) block.get("pos");
			List<IntTag> coords = coordTag.getValue();
			
			if (coords.get(0).getValue().equals(x) &&
					coords.get(1).getValue().equals(y) &&
					coords.get(2).getValue().equals(z)) {
				
				System.out.println(block.get("state"));
				
				// Get block from palette
				CompoundMap palleteBlock = palette[(Integer) block.get("state").getValue()];
				if (palleteBlock.get("Properties") == null) { // Properties may be null
					return new Block(
							(String) palleteBlock.get("Name").getValue(),
							new CompoundMap()
						);
				} else {
					return new Block(
							(String) palleteBlock.get("Name").getValue(),
							(CompoundMap) palleteBlock.get("Properties").getValue()
						);
				}
				
			}
		}
		return null;
	}

	@Override
	public int sizeX() {
		return sizeX;
	}

	@Override
	public int sizeY() {
		// TODO Auto-generated method stub
		return sizeY;
	}

	@Override
	public int sizeZ() {
		// TODO Auto-generated method stub
		return sizeZ;
	}
	
	/**
	 * Gets a list of all entities in structure, represented as CompoundMaps
	 * @return
	 */
	public List<CompoundMap> getEntities() {
		return entities;
	}
	
	public String toString() {
		return "Structure with size: "+sizeX+", "+sizeY+", "+sizeZ;
	}
	
	/**
	 * Load a structure from a compound map
	 * @param map Compound map
	 * @return Structure
	 */
	@SuppressWarnings("unchecked")
	public static Structure fromCompoundMap(CompoundMap map) {
		Structure structure = new Structure();
		
		// Load size
		ListTag<IntTag> sizeTag = (ListTag<IntTag>) map.get("size");
		if (sizeTag == null) {
			System.out.println("Structure missing size tag!");
			return null;
		}
		List<IntTag> sizeList = sizeTag.getValue();
		
		if (sizeList.get(0) == null || sizeList.get(1) == null || sizeList.get(2) == null) {
			System.out.println("Structure has improperly formatted size tag!");
			return null;
		}
		
		structure.sizeX = sizeList.get(0).getValue();
		structure.sizeY = sizeList.get(1).getValue();
		structure.sizeZ = sizeList.get(2).getValue();
				
		// Load palette
		ListTag<CompoundTag> paletteTag = (ListTag<CompoundTag>) map.get("palette");
		if (paletteTag == null) {
			System.out.println("Structure missing palette tag!");
			return null;
		}
		List<CompoundMap> palleteList = getValues(paletteTag);
		structure.palette = (CompoundMap[]) palleteList.toArray(new CompoundMap[palleteList.size()]);
				
		// Load blocks
		ListTag<CompoundTag> blocksTag = (ListTag<CompoundTag>) map.get("blocks");
		if (blocksTag == null) {
			System.out.println("Structure missing blocks tag!");
			return null;
		}
		List<CompoundMap> blockList = getValues(paletteTag);
		structure.blocks = (CompoundMap[]) getValues(blocksTag).toArray(new CompoundMap[blockList.size()]);
		
		// Load entities
		ListTag<CompoundTag> entitiesTag = (ListTag<CompoundTag>) map.get("entities");
		if (entitiesTag == null) {
			structure.entities = new ArrayList<CompoundMap>();
		} else {
			structure.entities = getValues(entitiesTag);
		}
		
		return structure;
	}
	
	/*
	 * Convert a ListTag of CompoundTags to a List of CompoundMaps
	 */
	private static List<CompoundMap> getValues(ListTag<CompoundTag> listTag) {
		List<CompoundMap> mapList = new ArrayList<CompoundMap>();
		List<CompoundTag> compoundTags = listTag.getValue();
		
		for (CompoundTag t : compoundTags) {
			mapList.add(t.getValue());
		}
		
		return mapList;
	}
	
	/**
	 * Load a structure from a file
	 * @param file File to load
	 * @return Structure
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static Structure fromFile(File file) throws FileNotFoundException, IOException {
		
		NBTInputStream input = new NBTInputStream(new FileInputStream(file));
		
		CompoundTag tag = (CompoundTag) input.readTag();
		CompoundMap map = (CompoundMap) tag.getValue();
		
		Structure structure = fromCompoundMap(map);
		input.close();
		
		return structure;
	}

}
