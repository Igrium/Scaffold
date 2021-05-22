package org.scaffoldeditor.scaffold.compile;

import java.nio.file.Path;
import java.util.Map;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;

public class CompileWorldStep implements CompileStep {

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args) {
		return level.compileBlockWorld(true);
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
