/**
 * Adapted from definition at https://minecraft.gamepedia.com/Schematic_file_format
 */

package org.metaversemedia.scaffold.nbt;

import java.util.List;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.StringTag;

/**
 * Class responsible for loading and managing Minecraft schematics
 * @author Sam54123
 *
 */
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
	 * @author Sam54123
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
	
	
	/**
	 * Create a Schematic from a Compound Map (nbt)
	 * @param map Compound Map to generate from
	 * @return Newly created Schematic
	 */
	public static Schematic fromCompoundMap(CompoundMap map) {
		if (map == null) {
			return null;
		}
		
		Schematic schematic = new Schematic();
		
		// Get width and height
		schematic.width = (short) map.get("Width").getValue();
		schematic.height = (short) map.get("Height").getValue();
		schematic.length = (short) map.get("Length").getValue();
		
		// Schematic may have not had proper tags
		if (schematic.width == 0 || schematic.height == 0 || schematic.length == 0) {
			System.out.println("Schematic is missing dimensions!");
			return null;
		}
		
		// Get materials
		StringTag materialTag = (StringTag) map.get("Materials");
		String materialName = materialTag.getValue();
		
		if (materialName.equals("Alpha")) {
			schematic.materials = MaterialType.ALPHA;
		} else if (materialName.equals("Pocket")) {
			schematic.materials = MaterialType.POCKET;
		} else if (materialName.equals("Classic")) {
			schematic.materials = MaterialType.CLASSIC;
		} else {
			System.out.println("Schematic materials tag is improperly formatted!");
			return null;
		}
		
		// Get blocks and data
		ByteArrayTag blocksArray = (ByteArrayTag) map.get("Blocks");
		schematic.blocks = blocksArray.getValue();
		
		ByteArrayTag dataArray = (ByteArrayTag) map.get("Data");
		schematic.data = dataArray.getValue();
		
		if (schematic.blocks == null || schematic.data == null) {
			System.out.println("Schematic blocks or data is improperly formatted!");
			return null;
		}
		
		// Get entities
		ListTag<CompoundTag> entities = (ListTag<CompoundTag>) map.get("Entities");
		schematic.entities = entities.getValue();
		
		ListTag<CompoundTag> tileEntities = (ListTag<CompoundTag>) map.get("TileEntities");
		schematic.tileEntities = tileEntities.getValue();
		
		// Entites are optional, so we don't check for success.
		
		
		return schematic;
	}
}
