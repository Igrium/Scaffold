package org.scaffoldeditor.scaffold.level.entity;

import org.scaffoldeditor.nbt.block.BlockWorld;

/**
 * Represents a Scaffold entity that has the ability to add Minecraft
 * entities into the world at compile time.
 * 
 * @author Igrium
 */
public interface EntityAdder {
	
	/**
	 * Compile this entity's game entities into the world.
	 * @param world World to compile into.
	 * @return Success.
	 */
	public boolean compileGameEntities(BlockWorld world);
}
