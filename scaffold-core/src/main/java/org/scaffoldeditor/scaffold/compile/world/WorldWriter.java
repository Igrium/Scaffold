package org.scaffoldeditor.scaffold.compile.world;

import java.io.File;
import java.io.IOException;

import org.scaffoldeditor.nbt.block.BlockWorld;

/**
 * A class that can write Minecraft .mca files.
 * @author Igrium
 */
public interface WorldWriter {
	
	/**
	 * Write a world out to file.
	 * @param regionFolder Region folder of the world.
	 * @param world Block world to write.
	 * @throws IOException If an IO exception occurs.
	 */
	void writeWorld(File regionFolder, BlockWorld world) throws IOException;
}
