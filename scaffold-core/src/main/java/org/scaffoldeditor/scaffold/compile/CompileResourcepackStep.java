package org.scaffoldeditor.scaffold.compile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileProgressListener;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.logic.Resourcepack;
import org.scaffoldeditor.scaffold.logic.AbstractPack.OutputMode;

public class CompileResourcepackStep implements CompileStep {

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args, CompileProgressListener listener) {
		if (!level.getEnableResourcepack()) return true;
		
		try {
			Resourcepack resourcepack = new Resourcepack(level.getProject());
			resourcepack.setDescription("Resources for "+level.getProject().getTitle());
			resourcepack.compile(target.resolve("resources").toFile(), OutputMode.ZIP);
		} catch (IOException e) {
			LogManager.getLogger().error("Error compiling resourcepack!", e);
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
