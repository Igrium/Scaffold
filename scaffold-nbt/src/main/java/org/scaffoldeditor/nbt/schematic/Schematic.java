/**
 * Adapted from definition at https://minecraft.gamepedia.com/Schematic_file_format
 */

package org.scaffoldeditor.nbt.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

/**
 * Class responsible for loading and managing Minecraft schematics
 * @author Igrium
 *
 */
@SuppressWarnings("unused")
public class Schematic {
	/* Actual storage of blocks */
	private byte[] blocks;
	
	/* Block data additionally defining parts of the terrain */
	private byte[] data;
	
	/* All entities in the schematic */
	private List<CompoundTag> entities;
	
	/* All tile entities in the schematic */
	private List<CompoundTag> tileEntities;
	
	/* Size along the X axis. */
	private short width;
	
	/* Size along the Y axis. */
	private short height;
	
	/* Size along the Z axis. */
	private short length;
	
	/* Schematic material type */
	private MaterialType materials;
	
	/**
	 * Schematic material types
	 * @author Igrium
	 *
	 */
	public enum MaterialType {
		CLASSIC, POCKET, ALPHA
	}
	
	/**
	 * Get the schematic's width.
	 * @return Width
	 */
	public short getWidth() {
		return width;
	}
	
	/**
	 * Get the schematic's height.
	 * @return Height
	 */
	public short getHeight() {
		return height;
	}
	
	/**
	 * Get the schematic's length.
	 * @return Length
	 */
	public short getLength() {
		return length;
	}
	
	public MaterialType materials() {
		return materials();
	}
	
	/**
	 * Get the byte representing the block at the given coordinates.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate.
	 * @return Bytes representing block
	 */
	public byte blockAt(int x, int y, int z) { 
		return blocks[(y*length + z)*width + x];
	}
	
	/**
	 * Get the block data of the block the given coordinates.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate.
	 * @return Block data.
	 */
	public byte dataAt(int x, int y, int z) {
		return data[(y*length + z)*width + x];
	}
	
	public byte dataAtIndex(int index) {
		return data[index];
	}
	
	
	/**
	 * Create a Schematic from a Compound Map (nbt)
	 * @param map Compound Map to generate from
	 * @return Newly created Schematic
	 */
	public static Schematic fromCompoundMap(CompoundTag map) {
		if (map == null) {
			throw new IllegalArgumentException("Schematic NBT can't be null!");
		}
		
		Schematic schematic = new Schematic();

		
		// Get width and height
		schematic.width = map.getShort("Width");
		schematic.height = map.getShort("Height");
		schematic.length = map.getShort("Length");
		
		// Schematic may have not had proper tags
		if (schematic.width == 0 || schematic.height == 0 || schematic.length == 0) {
			throw new IllegalArgumentException("Schematic is missing dimensions!");
		}
		
		// Get materials
		String materialName = map.getString("Materials");
		if (materialName == "") {
			schematic.materials = MaterialType.ALPHA;
		} else {			
			if (materialName.equals("Alpha")) {
				schematic.materials = MaterialType.ALPHA;
			} else if (materialName.equals("Pocket")) {
				schematic.materials = MaterialType.POCKET;
			} else if (materialName.equals("Classic")) {
				schematic.materials = MaterialType.CLASSIC;
			} else {
				throw new IllegalArgumentException("Schematic materials tag is improperly formatted!");
			}
		}
		
		// Get blocks and data
		schematic.blocks = map.getByteArray("Blocks");
		schematic.data = map.getByteArray("Data");
		
		if (schematic.blocks == null || schematic.data == null) {
			throw new IllegalArgumentException("Schematic blocks or data is improperly formatted!");
		}
		
		// Get entities
		ListTag<CompoundTag> entities = map.getListTag("Entities").asCompoundTagList();
		schematic.entities = new ArrayList<CompoundTag>();
		for (CompoundTag t : entities) {
			schematic.entities.add(t);
		}
		
		ListTag<CompoundTag> tileEntities = map.getListTag("TileEntities").asCompoundTagList();
		for (CompoundTag e : tileEntities) {
			schematic.tileEntities.add(e);
		}
		
		// Entites are optional, so we don't check for success.
		
		return schematic;
	}
}
