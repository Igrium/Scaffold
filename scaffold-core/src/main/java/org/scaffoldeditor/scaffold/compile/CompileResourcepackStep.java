package org.scaffoldeditor.scaffold.compile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.logic.Resourcepack;

public class CompileResourcepackStep implements CompileStep {

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args) {
		try {
			Resourcepack resourcepack = new Resourcepack(level.getProject().assetManager().getAbsolutePath("assets"));
			resourcepack.setDescription("Resources for "+level.getProject().getTitle());
			resourcepack.compile(target.resolve("resources"), true);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Assembling resourcepack...";
	}

	@Override
	public String getID() {
		return "compile_resourcepack";
	}

}
