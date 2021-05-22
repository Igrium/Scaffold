package org.scaffoldeditor.scaffold.compile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;

public class SetupStep implements CompileStep {

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args) {
		System.out.println("Target path: "+target);
		if (target.toFile().exists()) {
			try {
				FileUtils.deleteDirectory(target.toFile());
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		
		boolean cheats = false;
		if (args.get("cheats") instanceof BooleanAttribute) {
			cheats = ((BooleanAttribute) args.get("cheats")).getValue();
		}

		// Make world folder
		target.toFile().mkdir();

		// Compile levelData
		try {
			level.levelData().compileFile(target.resolve("level.dat").toFile(), cheats);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	@Override
	public boolean isRequired() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Initializing...";
	}

	@Override
	public String getID() {
		return "setup";
	}

}
