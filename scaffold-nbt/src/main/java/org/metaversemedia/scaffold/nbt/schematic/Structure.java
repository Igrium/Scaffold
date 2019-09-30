package org.metaversemedia.scaffold.nbt.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.metaversemedia.scaffold.nbt.Block;
import org.metaversemedia.scaffold.nbt.BlockCollection;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.stream.NBTInputStream;

/**
 * Represents a Minecraft structure schematic (.nbt)
 * @author Sam54123
 */
public class Structure implements BlockCollection {
	
	private CompoundMap[] palette;
	private List<CompoundMap> blocks;
	private List<CompoundMap> entities;
	
	private int sizeX;
	private int sizeY;
	private int sizeZ;
	
	/* A value from 0-3 that represents structure's rotation */
	private int rotation;
	
	private Structure() {};
	
	/**
	 * Get the name of the block at a particular location.
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block (NULL IF BLOCK IS VOID)
	 */
	@Override
	public Block blockAt(int x, int y, int z) {

		int state = getState(blockMapAt(x,y,z));

		if (state == -1) {
			return null;
		}
		
		// Get block from palette
		return Block.fromBlockPalleteEntry(palette[state]);
	}
	
	/**
	 * Get the index in the pallete of a block map
	 * @param block Block to check
	 * @return Palette index (-1 if non existant)
	 */
	private int getState(CompoundMap block) {
		if (block == null) {
			return -1;
		}

		return (Integer) block.get("state").getValue();
	}

	
	/**
	 * Get the compound map of the block at a set of coordinates
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block map (NULL IF BLOCK IS VOID)
	 */
	private CompoundMap blockMapAt(int x, int y, int z) {
		for (CompoundMap block : blocks) {
			@SuppressWarnings("unchecked")
			ListTag<IntTag> coordTag = (ListTag<IntTag>) block.get("pos");

			List<IntTag> coords = coordTag.getValue();

			if (coords.get(0).getValue().equals(x) &&
					coords.get(1).getValue().equals(y) &&
					coords.get(2).getValue().equals(z)) {
				return block;
			}
		}
		return null;
	}
	
	/**
	 * Get the structure's rotation
	 * @return An integer from 0 - 3 that represents the rotation
	 */
	public int getRotation() {
		return rotation;
	}
	
	/**
	 * Set the structure's rotation
	 * @param amount An integer from 0 - 3 that represents the rotation
	 */
	public void setRotation(int amount) {
		if (amount < 0 || amount > 3) {
			throw new IllegalArgumentException("Rotation amount must be an int between 0 and 3.");
		}
		
		rotate(amount - rotation);
	}
	
	/**
	 * Rotate the structure
	 * @param amount An integer from -3 - 3 that represents the amount to rotate by
	 */
	public void rotate(int amount) {
		if (amount < -3 || amount > 3) {
			throw new IllegalArgumentException("Rotation amount must be an int between -3 and 3.");
		}
		
		if (amount < 0) {
			amount = 4 - (amount*-1);
		}
		
		// Make backup of blocks to reference from
		List<CompoundMap> oldBlocks = new ArrayList<CompoundMap>();
		Collections.copy(oldBlocks, blocks);
		
		// Translate all blocks to new location
		for (CompoundMap block : blocks) {
			@SuppressWarnings("unchecked")
			ListTag<IntTag> coordTag = (ListTag<IntTag>) block.get("pos");
			List<IntTag> coords = coordTag.getValue();
			
			// Rotate around Y axis, affecting x and z coordinates
			int[] newCoords = rotatePoint(coords.get(0).getValue(), coords.get(2).getValue(),
					sizeX/2, sizeZ/2, amount);
			
			coords.set(0, new IntTag("", newCoords[0]));
			coords.set(2, new IntTag("", newCoords[2]));
		}
		
		// Rotate all applicable blocks in palette
		Map<Integer, String> rotations = new HashMap<Integer, String>();
		rotations.put(0, "north");
		rotations.put(1, "west");
		rotations.put(2, "south");
		rotations.put(3, "east");
		
		for (CompoundMap paletteBlock : palette) {
			if (paletteBlock.containsKey("Properties")) {
				CompoundTag propertiesTag = (CompoundTag) paletteBlock.get("Properties");
				CompoundMap properties = propertiesTag.getValue();
				if (properties.containsKey("facing")) {
					StringTag facing = (StringTag) properties.get("facing");

					int facingKey = getKeyByValue(rotations, facing.getValue());
					facingKey += amount;
					
					if (facingKey > 3) { // If true, it has gone 360* around.
						facingKey -= 4;
					} else if (facingKey < 0) {
						facingKey += 4;
					}
					
					properties.put(new StringTag("facing", rotations.get(facingKey)));
				}
			}
		}
		
		rotation += amount;
		
		if (rotation > 3) { // If true, it has gone 360* around.
			rotation -= 4;
		} else if (rotation < 0) {
			rotation += 4;
		}
	}
	
	/*
	 * Get a map's key by it's value
	 * Adapted from: https://stackoverflow.com/a/2904266/5676620
	 */
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
	/* Utility function to rotate a point incriments of 90 degrees around anotner
	 * Rotation amount is determined by amount*90
	 */
	private static int[] rotatePoint(int pointX, int pointY, int centerX, int centerY, int amount) {
		// Translate point so center is in origin
		pointX -= centerX;
		pointY -= centerY;
		
		int[] newPoint = null;
		if (amount == 0) {
			newPoint = new int[] {pointX, pointY};
		} else if (amount == 1) {
			newPoint = new int[] {pointY*-1, pointX};
		}  else if (amount == 2) {
			newPoint = new int[] {pointX*-1, pointY*-1};
		} else if (amount == 3) {
			newPoint = new int[] {pointY, pointX*-1};
		} else {
			throw new IllegalArgumentException("Rotation amount must be an int between 0 and 3.");
		}
		
		// Translate back
		return new int[] {newPoint[0]+centerX, newPoint[1]+centerY};
	}
	
	/**
	 * Set a block state at a certian location
	 * (private because it relies on the state)
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param state Index in palette of new block (-1 for deleting block)
	 */
	private void setBlockAt(int x, int y, int z, int state) {
		CompoundMap blockMap = blockMapAt(x,y,z);
		
		// If block  doesn't already exist
		if (blockMap == null) {
			if (state == -1) {
				return;
			}
			// Create new block
			blockMap = new CompoundMap();
			
			List<IntTag> coords = new ArrayList<IntTag>();
			coords.add(new IntTag("", x));
			coords.add(new IntTag("", y));
			coords.add(new IntTag("", z));
			blockMap.put(new ListTag<IntTag>("pos", IntTag.class, coords));
			
			blockMap.put(new IntTag("state", state));
			blocks.add(blockMap);
			
			
		} else { // Block already exists
			blockMap.remove("state");
			blockMap.put(new IntTag("state", state));
		}
	}
	

	public int sizeX() {
		return sizeX;
	}

	public int sizeY() {
		return sizeY;
	}

	public int sizeZ() {
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
		structure.blocks = getValues(blocksTag);
		
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
