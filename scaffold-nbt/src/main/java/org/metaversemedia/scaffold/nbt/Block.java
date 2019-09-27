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
}
