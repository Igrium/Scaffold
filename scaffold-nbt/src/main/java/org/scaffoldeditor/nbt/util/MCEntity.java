package org.scaffoldeditor.nbt.util;

import net.querz.nbt.tag.CompoundTag;

/**
 * Represents a Minecraft entity, either vanilla or modded.
 * @author Igrium
 */
public class MCEntity {
	private String id;
	private CompoundTag nbt;
	
	/**
	 * Create a Minecraft entity struct.
	 * @param id Namespaced name of the entity.
	 * @param nbt The entity's NBT data.
	 */
	public MCEntity(String id, CompoundTag nbt) {
		this.id = id;
		this.nbt = nbt;
	}
	
	/**
	 * Get the namespaced name of the entity.
	 * @return Namespaced name (minecraft:[name])
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Get the entity's NBT data.
	 * @return Entity NBT data.
	 */
	public CompoundTag getNBT() {
		return nbt;
	}
	
	@Override
	public String toString() {
		return "MCEntity[id:'"+id+", nbt:"+nbt+"]";
	}
}
