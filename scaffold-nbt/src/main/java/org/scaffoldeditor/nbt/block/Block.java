package org.scaffoldeditor.nbt.block;

import java.util.Objects;

import net.querz.nbt.tag.CompoundTag;

/**
 * Represents a single Minecraft block and its data.
 * @author h205p1
 */
public class Block {
	String name;
	CompoundTag properties;
	
	/**
	 * Create a Block object.
	 * @param name Namespaced name.
	 * @param properties Block properties.
	 * (must be converted for pre-flattening blocks)
	 */
	public Block(String name, CompoundTag properties) {
		this.name = name;
		this.properties = properties;
	}
	
	/**
	 * Create a block object.
	 * @param name Namespaced name.
	 */
	public Block(String name) {
		this(name, new CompoundTag());
	}
	
	public String getName() {
		return name;
	}
	
	public CompoundTag getProperties() {
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
	public static Block fromBlockPalleteEntry(CompoundTag paletteEntry) {
		if (paletteEntry.get("Properties") == null) {
			return new Block(paletteEntry.getString("Name"));
		} else {
			return new Block(paletteEntry.getString("Name"), paletteEntry.getCompoundTag("Properties"));
		}
	}
	
	/**
	 * Convert this Block into a palette entry.
	 * @return Palette Entry.
	 */
	public CompoundTag toPaletteEntry() {
		CompoundTag paletteEntry = new CompoundTag();
		if (this.getProperties().size() > 0) {
			paletteEntry.put("Properties", properties);
		}
		paletteEntry.putString("Name", this.name);
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
