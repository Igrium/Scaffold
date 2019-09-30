package org.metaversemedia.scaffold.nbt;

import com.flowpowered.nbt.CompoundMap;

/**
 * Represents a single Minecraft block and its data.
 * @author h205p1
 */
public class Block {
	String name;
	CompoundMap properties;
	
	/**
	 * Create a Block object
	 * @param name Namespaced name
	 * @param properties Block properties 
	 * (must be converted for pre-flattening blocks)
	 */
	public Block(String name, CompoundMap properties) {
		this.name = name;
		this.properties = properties;
	}
	
	public String getName() {
		return name;
	}
	
	public CompoundMap getProperties() {
		return properties;
	}
	
	public String toString() {
		return "Block: "+name;
	}
	
	/**
	 * Create a block object from a palette entry (as defined in Minecraft structure format)
	 * @param paletteEntry
	 * @return
	 */
	public static Block fromBlockPalleteEntry(CompoundMap paletteEntry) {
		if (paletteEntry.get("Properties") == null) { // Properties may be null
			return new Block((String) paletteEntry.get("Name").getValue(), new CompoundMap());
		} else {
			return new Block((String) paletteEntry.get("Name").getValue(),
					(CompoundMap) paletteEntry.get("Properties").getValue());
		}
	}
}
