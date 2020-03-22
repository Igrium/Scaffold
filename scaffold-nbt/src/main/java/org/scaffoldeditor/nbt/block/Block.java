package org.scaffoldeditor.nbt.block;

import java.util.HashMap;
import java.util.Objects;

import com.github.mryurihi.tbnbt.tag.NBTTag;
import com.github.mryurihi.tbnbt.tag.NBTTagCompound;
import com.github.mryurihi.tbnbt.tag.NBTTagString;

/**
 * Represents a single Minecraft block and its data.
 * @author h205p1
 */
public class Block {
	String name;
	NBTTagCompound properties;
	
	/**
	 * Create a Block object.
	 * @param name Namespaced name.
	 * @param properties Block properties.
	 * (must be converted for pre-flattening blocks)
	 */
	public Block(String name, NBTTagCompound properties) {
		this.name = name;
		this.properties = properties;
	}
	
	/**
	 * Create a block object.
	 * @param name Namespaced name.
	 */
	public Block(String name) {
		this(name, new NBTTagCompound(new HashMap<String, NBTTag>()));
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
	
	/**
	 * Convert this Block into a palette entry.
	 * @return PaletteEntry.
	 */
	public NBTTagCompound toPaletteEntry() {
		NBTTagCompound paletteEntry = new NBTTagCompound(new HashMap<String, NBTTag>());
		if (this.properties.getValue().keySet().size() != 0) {
			paletteEntry.put("Properties", properties);
		}
		
		paletteEntry.put("Name", new NBTTagString(this.name));
		return paletteEntry;
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

	@Override
	public int hashCode() {
		return Objects.hash(name, properties);
	}

}
