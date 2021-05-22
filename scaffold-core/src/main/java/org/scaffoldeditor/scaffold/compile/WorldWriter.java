package org.scaffoldeditor.scaffold.compile;

import java.io.File;
import java.io.IOException;

import org.scaffoldeditor.nbt.block.BlockWorld;

public interface WorldWriter {
	void writeWorld(File regionFolder, BlockWorld world) throws IOException;
}
