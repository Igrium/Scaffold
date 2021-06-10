package org.scaffoldeditor.scaffold.compile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.scaffoldeditor.scaffold.compile.Compiler.CompileProgressListener;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.util.PythonUtils;

public class PostCompileScripts implements CompileStep {

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args, CompileProgressListener listener) {
		Project project = level.getProject();
		if (project.gameInfo().preCompileScripts.isEmpty()) {
			return true;
		}
		if (!PythonUtils.isPythonInstalled()) {
			listener.onError("Unable to run scripts because Python is not installed!");
			return false;
		}
		for (String scriptName : project.gameInfo().postCompileScripts) {
			File absoluteScript = project.assetManager().getAbsoluteFile(scriptName);
			try {
				int exitCode = PythonUtils.runScript(PythonUtils.POST_COMPILE_SCRIPT, project.getProjectFolder().toFile(), absoluteScript.toString(),
						target.toString(), project.getProjectFolder().toString(), level.getName());
				System.out.println("Script "+scriptName+" exited with code "+exitCode);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Running post-compile scripts";
	}

	@Override
	public String getID() {
		return "postCompileScripts";
	}

}
