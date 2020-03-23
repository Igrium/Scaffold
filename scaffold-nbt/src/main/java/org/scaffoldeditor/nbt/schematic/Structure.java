package org.scaffoldeditor.nbt.schematic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockReader;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;

import com.github.mryurihi.tbnbt.stream.NBTInputStream;
import com.github.mryurihi.tbnbt.tag.NBTTag;
import com.github.mryurihi.tbnbt.tag.NBTTagCompound;
import com.github.mryurihi.tbnbt.tag.NBTTagInt;
import com.github.mryurihi.tbnbt.tag.NBTTagList;
import com.github.mryurihi.tbnbt.tag.NBTTagString;

import java.util.Objects;

/**
 * Represents a Minecraft structure schematic (.nbt)
 * @author Sam54123
 */
public class Structure implements SizedBlockCollection, BlockReader {
	
	private NBTTagCompound[] palette;
	private List<NBTTagCompound> blocks;
	private List<NBTTagCompound> entities;
	
	private int sizeX;
	private int sizeY;
	private int sizeZ;
	
	/* A value from 0-3 that represents structure's rotation */
	private int rotation;
		
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
	private int getState(NBTTagCompound block) {
		if (block == null) {
			return -1;
		}

		return ((NBTTagInt) block.get("state")).getValue();
	}

	
	/**
	 * Get the compound map of the block at a set of coordinates
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block map (NULL IF BLOCK IS VOID)
	 */
	private NBTTagCompound blockMapAt(int x, int y, int z) {
		for (NBTTagCompound block : blocks) {
			NBTTagList coordTag = (NBTTagList) block.get("pos");

			List<NBTTagInt> coords = new ArrayList<NBTTagInt>();
			for (NBTTag t : coordTag.getValue()) {
				coords.add((NBTTagInt) t);
			}
			
			if (coords.get(0).getValue() == x &&
					coords.get(1).getValue() == y &&
					coords.get(2).getValue() == z) {
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
		List<NBTTagCompound> oldBlocks = new ArrayList<NBTTagCompound>();
		Collections.copy(oldBlocks, blocks);
		
		// Translate all blocks to new location
		for (NBTTagCompound block : blocks) {
			NBTTagList coordTag = (NBTTagList) block.get("pos");
			List<NBTTagInt> coords = new ArrayList<NBTTagInt>();
			for (NBTTag t : coordTag.getValue()) {
				coords.add((NBTTagInt) t);
			}
						
			// Rotate around Y axis, affecting x and z coordinates
			int[] newCoords = rotatePoint(coords.get(0).getValue(), coords.get(2).getValue(),
					sizeX/2, sizeZ/2, amount);
			
			coords.set(0, new NBTTagInt(newCoords[0]));
			coords.set(2, new NBTTagInt(newCoords[2]));
		}
		
		// Rotate all applicable blocks in palette
		Map<Integer, String> rotations = new HashMap<Integer, String>();
		rotations.put(0, "north");
		rotations.put(1, "west");
		rotations.put(2, "south");
		rotations.put(3, "east");
		
		for (NBTTagCompound paletteBlock : palette) {
			if (paletteBlock.containsKey("Properties")) {
				NBTTagCompound properties = (NBTTagCompound) paletteBlock.get("Properties");
				if (properties.containsKey("facing")) {
					NBTTagString facing = (NBTTagString) properties.get("facing");

					int facingKey = getKeyByValue(rotations, facing.getValue());
					facingKey += amount;
					
					if (facingKey > 3) { // If true, it has gone 360* around.
						facingKey -= 4;
					} else if (facingKey < 0) {
						facingKey += 4;
					}
					
					properties.put("facing", new NBTTagString(rotations.get(facingKey)));
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
	 * Set a block state at a certain location
	 * (private because it relies on the state)
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param state Index in palette of new block (-1 for deleting block)
	 */
	@SuppressWarnings("unused")
	private void setBlockAt(int x, int y, int z, int state) {
		NBTTagCompound blockMap = blockMapAt(x,y,z);
		
		// If block  doesn't already exist
		if (blockMap == null) {
			if (state == -1) {
				return;
			}
			// Create new block
			blockMap = new NBTTagCompound(new HashMap<String, NBTTag>());
			
			List<NBTTag> coords = new ArrayList<NBTTag>();
			coords.add(new NBTTagInt(x));
			coords.add(new NBTTagInt(y));
			coords.add(new NBTTagInt(z));
			blockMap.put("pos", new NBTTagList(coords));
			
			blockMap.put("state", new NBTTagInt(state));
			blocks.add(blockMap);
			
			
		} else { // Block already exists
			blockMap.getValue().remove("state");
			blockMap.put("state", new NBTTagInt(state));
		}
	}
	
	@Override
	public int getWidth() {
		return sizeX;
	}

	@Override
	public int getHeight() {
		return sizeY;
	}

	@Override
	public int getLength() {
		return sizeZ;
	}
	
	/**
	 * Gets a list of all entities in structure, represented as CompoundMaps
	 * @return
	 */
	public List<NBTTagCompound> getEntities() {
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
	public static Structure fromCompoundMap(NBTTagCompound map) {
		Structure structure = new Structure();
		
		// Load size
		NBTTagList sizeTag = (NBTTagList) map.get("size");
		if (sizeTag == null) {
			System.out.println("Structure missing size tag!");
			return null;
		}
		List<NBTTagInt> sizeList = new ArrayList<NBTTagInt>();
		for (NBTTag t : sizeTag.getValue()) {
			sizeList.add((NBTTagInt) t);
		}
		
		if (sizeList.get(0) == null || sizeList.get(1) == null || sizeList.get(2) == null) {
			System.out.println("Structure has improperly formatted size tag!");
			return null;
		}
		
		structure.sizeX = sizeList.get(0).getValue();
		structure.sizeY = sizeList.get(1).getValue();
		structure.sizeZ = sizeList.get(2).getValue();
				
		// Load palette
		NBTTagList paletteTag = (NBTTagList) map.get("palette");
		if (paletteTag == null) {
			System.out.println("Structure missing palette tag!");
			return null;
		}
		List<NBTTagCompound> palleteList = getCompoundMaps(paletteTag);
		structure.palette = (NBTTagCompound[]) palleteList.toArray(new NBTTagCompound[palleteList.size()]);
				
		// Load blocks
		NBTTagList blocksTag = (NBTTagList) map.get("blocks");
		if (blocksTag == null) {
			System.out.println("Structure missing blocks tag!");
			return null;
		}
		structure.blocks = getCompoundMaps(blocksTag);
		
		// Load entities
		NBTTagList entitiesTag = (NBTTagList) map.get("entities");
		if (entitiesTag == null) {
			structure.entities = new ArrayList<NBTTagCompound>();
		} else {
			structure.entities = getCompoundMaps(entitiesTag);
		}
		
		return structure;
	}
	
	/*
	 * Convert a ListTag of CompoundTags to a List of CompoundMaps
	 */
	private static List<NBTTagCompound> getCompoundMaps(NBTTagList listTag) {
		List<NBTTagCompound> mapList = new ArrayList<NBTTagCompound>();
		for (NBTTag t : listTag.getValue()) {
			mapList.add((NBTTagCompound) t);
		}
		return mapList;
	}


	@Override
	public Iterator<Block> iterator() {
		return new Iterator<Block>() {
			
			private int head = 0;
			
			@Override
			public boolean hasNext() {
				return head < blocks.size();
			}

			@Override
			public Block next() {
				int state = getState(blocks.get(head));
				head++;
				return Block.fromBlockPalleteEntry(palette[state]);
			}
		};
	}

	@Override
	public SizedBlockCollection readBlockCollection(InputStream in) throws IOException {
		NBTInputStream input = new NBTInputStream(in);
		NBTTagCompound map = input.readTag().getAsTagCompound();
		input.close();
		
		if (map == null) {
			throw new IOException("Improperly formatted structure file!");
		}
		
		return fromCompoundMap(map);
	}
}
