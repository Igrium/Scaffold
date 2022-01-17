package org.scaffoldeditor.scaffold.compile;

import java.nio.file.Path;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileProgressListener;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;

public class CompileWorldStep implements CompileStep {

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args, CompileProgressListener listener) {
		try {
			level.compileBlockWorld(true);
			return true;
		} catch (Throwable e) {
			LogManager.getLogger().error("Unable to compile block world.", e);
			return false;
		}
	}

	@Override
	public boolean isRequired() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Placing blocks...";
	}

	@Override
	public String getID() {
		return "compile_blockworld";
	}

}
