package org.scaffoldeditor.scaffold.compile;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.scaffoldeditor.scaffold.compile.Compiler.CompileProgressListener;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;

public class CompileWorldStep implements CompileStep {

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args, CompileProgressListener listener) {
		Future<Boolean> future = level.getProject().getLevelService().submit(() -> {
			try {
				level.compileBlockWorld(true);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		});
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
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
		return "Placing blocks...";
	}

	@Override
	public String getID() {
		return "compile_blockworld";
	}

}
