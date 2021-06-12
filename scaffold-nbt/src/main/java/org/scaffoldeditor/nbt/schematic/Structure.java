package org.scaffoldeditor.nbt.schematic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockReader;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.math.Vector3i;

import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a Minecraft structure schematic (.nbt)
 * @author Igrium
 */
public class Structure implements SizedBlockCollection, BlockReader<Structure> {
	
	private CompoundTag[] palette;
	private List<CompoundTag> blocks;
	private List<CompoundTag> entities;
	private Map<Vector3i, CompoundTag> blockEntities = new HashMap<>();
	
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
	private int getState(CompoundTag block) {
		if (block == null) {
			return -1;
		}

		return (block.getIntTag("state")).asInt();
	}

	
	/**
	 * Get the compound map of the block at a set of coordinates
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block map (NULL IF BLOCK IS VOID)
	 */
	private CompoundTag blockMapAt(int x, int y, int z) {
		for (CompoundTag block : blocks) {
			ListTag<IntTag> coordTag = block.getListTag("pos").asIntTagList();
			List<Integer> coords = new ArrayList<>();
			
			for (IntTag t : coordTag) {
				coords.add(t.asInt());
			}
			
			if (coords.get(0) == x &&
					coords.get(1) == y &&
					coords.get(2) == z) {
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
		List<CompoundTag> oldBlocks = new ArrayList<>();
		Collections.copy(oldBlocks, blocks);
		
		// Translate all blocks to new location
		for (CompoundTag block : blocks) {
			ListTag<IntTag> coords = block.getListTag("pos").asIntTagList();
	
						
			// Rotate around Y axis, affecting x and z coordinates
			int[] newCoords = rotatePoint(coords.get(0).asInt(), coords.get(2).asInt(),
					sizeX/2, sizeZ/2, amount);
			
			coords.set(0, new IntTag(newCoords[0]));
			coords.set(2, new IntTag(newCoords[2]));
		}
		
		// Rotate all applicable blocks in palette
		Map<Integer, String> rotations = new HashMap<Integer, String>();
		rotations.put(0, "north");
		rotations.put(1, "west");
		rotations.put(2, "south");
		rotations.put(3, "east");
		
		for (CompoundTag paletteBlock : palette) {
			if (paletteBlock.containsKey("Properties")) {
				CompoundTag properties = paletteBlock.getCompoundTag("Properties");
				if (properties.containsKey("facing")) {
					String facing = properties.getString("facing");

					int facingKey = getKeyByValue(rotations, facing);
					facingKey += amount;
					
					if (facingKey > 3) { // If true, it has gone 360* around.
						facingKey -= 4;
					} else if (facingKey < 0) {
						facingKey += 4;
					}
					
					properties.putString("facing", rotations.get(facingKey));
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
		CompoundTag blockMap = blockMapAt(x,y,z);
		
		// If block  doesn't already exist
		if (blockMap == null) {
			if (state == -1) {
				return;
			}
			// Create new block
			blockMap = new CompoundTag();
			
			ListTag<IntTag> coords = new ListTag<>(IntTag.class);
			coords.add(new IntTag(x));
			coords.add(new IntTag(y));
			coords.add(new IntTag(z));
			blockMap.put("pos", coords);
			
			blockMap.put("state", new IntTag(state));
			blocks.add(blockMap);
			
			
		} else { // Block already exists
			blockMap.remove("state");
			blockMap.put("state", new IntTag(state));
		}
	}
	
	@Override
	public List<CompoundTag> getEntities() {
		return entities;
	}
	
	@Override
	public Set<Vector3i> getBlockEntities() {
		return blockEntities.keySet();
	}
	
	@Override
	public CompoundTag blockEntityAt(Vector3i vec) {
		return blockEntities.get(vec);
	}
	
	private static Vector3i decodeLocation(ListTag<IntTag> tag) {
		return new Vector3i(tag.get(0).asInt(), tag.get(1).asInt(), tag.get(2).asInt());
	}
		
	
	public String toString() {
		return "Structure with size: "+sizeX+", "+sizeY+", "+sizeZ;
	}
	
	/**
	 * Load a structure from a compound tag
	 * @param map Compound tag
	 * @return Structure
	 */
	public static Structure fromCompoundMap(CompoundTag map) {
		Structure structure = new Structure();
		
		// Load size
		ListTag<IntTag> sizeList = map.getListTag("size").asIntTagList();
		if (sizeList == null) {
			throw new IllegalArgumentException("Structure missing size tag!");
		}
		
		
		if (sizeList.get(0) == null || sizeList.get(1) == null || sizeList.get(2) == null) {
			throw new IllegalArgumentException("Structure has improperly formatted size tag!");
		}
		
		structure.sizeX = sizeList.get(0).asInt();
		structure.sizeY = sizeList.get(1).asInt();
		structure.sizeZ = sizeList.get(2).asInt();
				
		// Load palette
		ListTag<CompoundTag> paletteTag = map.getListTag("palette").asCompoundTagList();
		if (paletteTag == null) {
			throw new IllegalArgumentException("Structure missing palette tag!");
		}
		List<CompoundTag> palleteList = getCompoundMaps(paletteTag);
		structure.palette = palleteList.toArray(new CompoundTag[palleteList.size()]);
				
		// Load blocks
		ListTag<CompoundTag> blocksTag = map.getListTag("blocks").asCompoundTagList();
		if (blocksTag == null) {
			throw new IllegalArgumentException("Structure missing blocks tag!");
		}
		structure.blocks = getCompoundMaps(blocksTag);
		
		// Load entities
		ListTag<CompoundTag> entitiesTag = map.getListTag("entities").asCompoundTagList();
		if (entitiesTag == null) {
			structure.entities = new ArrayList<CompoundTag>();
		} else {
			structure.entities = getCompoundMaps(entitiesTag);
		}
		
		// Load block entities
		for (CompoundTag block : structure.blocks) {
			if (block.containsKey("nbt")) {
				structure.blockEntities.put(decodeLocation(block.getListTag("pos").asIntTagList()), block.getCompoundTag("nbt"));
			}
		}
		
		return structure;
	}
	
	/*
	 * Convert a ListTag of CompoundTags to a List of CompoundMaps
	 */
	private static List<CompoundTag> getCompoundMaps(ListTag<CompoundTag> listTag) {
		List<CompoundTag> mapList = new ArrayList<>();
		for (CompoundTag t : listTag) {
			mapList.add(t);
		}
		return mapList;
	}

	@Override
	public Structure readBlockCollection(InputStream in) throws IOException {
		CompoundTag map = (CompoundTag) new NBTDeserializer(true).fromStream(in).getTag();
		
		if (map == null) {		
			throw new IOException("Improperly formatted structure file!");
		}
		
		return fromCompoundMap(map);
	}

	@Override
	public Vector3i getMin() {
		return new Vector3i(0, 0, 0);
	}

	@Override
	public Vector3i getMax() {
		return new Vector3i(sizeX, sizeY, sizeZ);
	}
}
