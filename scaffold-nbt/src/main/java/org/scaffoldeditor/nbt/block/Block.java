package org.scaffoldeditor.nbt.block;

import java.util.HashMap;

import mryurihi.tbnbt.tag.NBTTag;
import mryurihi.tbnbt.tag.NBTTagCompound;
import mryurihi.tbnbt.tag.NBTTagString;

/**
 * Represents a single Minecraft block and its data.
 * @author h205p1
 */
public class Block {
	String name;
	NBTTagCompound properties;
	
	/**
	 * Create a Block object
	 * @param name Namespaced name
	 * @param properties Block properties 
	 * (must be converted for pre-flattening blocks)
	 */
	public Block(String name, NBTTagCompound properties) {
		this.name = name;
		this.properties = properties;
	}
	
	public String getName() {
		return name;
	}
	
	public NBTTagCompound getProperties() {
		return properties;
	}
	
	public String toString() {
		return "Block: "+name;
	}
	
	/**
	 * Create a block object from a palette entry (as defined in Minecraft structure format).
	 * @param paletteEntry.
	 * @return
	 */
	public static Block fromBlockPalleteEntry(NBTTagCompound paletteEntry) {
		if (paletteEntry.get("Properties") == null) { // Properties may be null
			return new Block(((NBTTagString) paletteEntry.get("Name")).getValue(), new NBTTagCompound(new HashMap<String, NBTTag>()));
		} else {
			return new Block(((NBTTagString) paletteEntry.get("Name")).getValue(),
					(NBTTagCompound) paletteEntry.get("Properties"));
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		Block blockObj = (Block) obj;
		if (blockObj == null) {
			return false;
		}
		
		return (blockObj.name.matches(name)
				&& blockObj.properties.equals(properties));

	}
}
