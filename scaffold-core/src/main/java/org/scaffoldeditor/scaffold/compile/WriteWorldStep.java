package org.scaffoldeditor.scaffold.compile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.scaffoldeditor.scaffold.compile.world.NativeWorldWriter;
import org.scaffoldeditor.scaffold.compile.world.WorldWriter;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;

public class WriteWorldStep implements CompileStep {
	
	private static WorldWriter worldWriter = new NativeWorldWriter();

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args) {
		File regionFolder = target.resolve("region").toFile();
		regionFolder.mkdir();
		
		try {
			worldWriter.writeWorld(regionFolder, level.getBlockWorld());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isRequired() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Writing world...";
	}

	@Override
	public String getID() {
		return "write_world";
	}

	public static WorldWriter getWorldWriter() {
		return worldWriter;
	}
	
	/**
	 * Set the world writer that the compiler will use when compiling worlds.
	 */
	public static void setWorldWriter(WorldWriter worldWriter) {
		WriteWorldStep.worldWriter = worldWriter;
	}

}
