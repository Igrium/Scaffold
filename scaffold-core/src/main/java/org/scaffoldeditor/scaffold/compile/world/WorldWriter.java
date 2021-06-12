package org.scaffoldeditor.scaffold.compile.world;

import java.io.IOException;
import java.nio.file.Path;

import org.scaffoldeditor.nbt.block.BlockWorld;

/**
 * A class that can write Minecraft .mca files.
 * @author Igrium
 */
public interface WorldWriter {
	
	/**
	 * Write a world out to file.
	 * @param worldFolder Folder of the world.
	 * @param world Block world to write.
	 * @throws IOException If an IO exception occurs.
	 */
	void writeWorld(Path worldFolder, BlockWorld world) throws IOException;
}
