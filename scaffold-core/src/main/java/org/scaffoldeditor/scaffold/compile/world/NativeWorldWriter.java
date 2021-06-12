package org.scaffoldeditor.scaffold.compile.world;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.scaffoldeditor.nbt.Constants;
import org.scaffoldeditor.nbt.block.BlockWorld;

public class NativeWorldWriter implements WorldWriter {

	@Override
	public void writeWorld(Path worldFolder, BlockWorld world) throws IOException {
		File regionFolder = worldFolder.resolve("region").toFile();
		File entitiesFolder = worldFolder.resolve("entities").toFile();
		
		regionFolder.mkdir();
		entitiesFolder.mkdir();
		
		world.serialize(regionFolder, entitiesFolder, Constants.DEFAULT_DATA_VERSION);
	}

}
