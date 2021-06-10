package org.scaffoldeditor.scaffold.compile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileProgressListener;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.util.PythonUtils;

public class ScriptStep implements CompileStep {
	
	public final List<String> scripts;
	public final String callScript;
	public final String id;
	
	public ScriptStep(List<String> scripts, String callScript, String id) {
		this.scripts = scripts;
		this.callScript = callScript;
		this.id = id;
	}

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args, CompileProgressListener listener) {
		Project project = level.getProject();
		LogManager.getLogger().debug(scripts);
		if (scripts.isEmpty()) return true;
		
		if (!PythonUtils.isPythonInstalled()) {
			listener.onError("Unable to run scripts because Python is not installed!");
			return false;
		}
		
		Consumer<String> onReadLine = new Consumer<String>() {

			@Override
			public void accept(String t) {
				listener.println("[python] "+t);
			}
		};
		
		boolean success = true;
		for (String scriptName : scripts) {
			File absoluteScript = PythonUtils.resolveScript(project, scriptName);
			try {
				int exitCode = PythonUtils.runScript(callScript, project.getProjectFolder().toFile(), onReadLine,
						absoluteScript.toString(), target.toString(), project.getProjectFolder().toString(),
						level.getName());
				LogManager.getLogger().info("Script "+scriptName+" exited with code "+exitCode);
				if (exitCode != 0) {
					success = false;
					listener.onError("Script: "+scriptName+" failed!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Running compile scripts";
	}

	@Override
	public String getID() {
		return id;
	}

}
