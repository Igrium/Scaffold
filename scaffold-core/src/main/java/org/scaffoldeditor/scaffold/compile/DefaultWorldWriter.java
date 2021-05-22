package org.scaffoldeditor.scaffold.compile;

import java.io.File;
import java.io.IOException;

import org.scaffoldeditor.nbt.Constants;
import org.scaffoldeditor.nbt.block.BlockWorld;

public class DefaultWorldWriter implements WorldWriter {

	@Override
	public void writeWorld(File regionFolder, BlockWorld world) throws IOException {
		world.serialize(regionFolder, Constants.DEFAULT_DATA_VERSION);
	}

}
