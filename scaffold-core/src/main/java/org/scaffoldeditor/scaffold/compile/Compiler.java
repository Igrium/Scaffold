package org.scaffoldeditor.scaffold.compile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.util.PythonUtils;

public class Compiler {
	
	public enum CompileEndStatus { FINISHED, FAILED, CANCELED };
	
	public static class CompileResult {
		public final CompileEndStatus endStatus;
		public final String errorMessage;
		
		public CompileResult(CompileEndStatus endStatus, String errorMessage) {
			this.endStatus = endStatus;
			this.errorMessage = errorMessage;
		}
		
		public static CompileResult successfulCompile() {
			return new CompileResult(CompileEndStatus.FINISHED, "");
		}
		
		public static CompileResult canceled() {
			return new CompileResult(CompileEndStatus.CANCELED, "");
		}
	}
	
	public static interface CompileProgressListener {	
		/**
		 * Called when a new step is started.
		 * @param percent Percentage of the compile that's complete.
		 * @param description Text to show in the UI.
		 */
		void onCompileProgress(float percent, String description);
		void println(String string);
		void onError(String description);
	}
	
	public static Compiler getDefault(Project project) {
		Compiler compiler = new Compiler();
		
		compiler.steps.add(new SetupStep());
		compiler.steps.add(new ScriptStep(project.gameInfo().preCompileScripts, PythonUtils.PRE_COMPILE_SCRIPT,
				"preCompileScripts"));
		compiler.steps.add(new CompileWorldStep());
		compiler.steps.add(new WriteWorldStep());
		compiler.steps.add(new CompileLogicStep());
		compiler.steps.add(new CompileResourcepackStep());
		compiler.steps.add(new ScriptStep(project.gameInfo().postCompileScripts, PythonUtils.POST_COMPILE_SCRIPT,
				"postCompileScripts"));
		
		return compiler;
	}
	
	/**
	 * The compile steps this compiler will execute, in order.
	 */
	public final List<CompileStep> steps = new ArrayList<>();
	
	private boolean shouldCancel = false;
	private boolean isActive = false;
	
	/**
	 * Compile a level.
	 * @param level Level to compile.
	 * @param target Folder to compile into (the world folder itself).
	 * @param arguements Compile arguements.
	 * @param listener Listener to update the UI.
	 * @return The result of the compile.
	 */
	public CompileResult compile(Level level, Path target, Map<String, Attribute<?>> arguements, CompileProgressListener listener) {
		this.isActive = true;
		this.shouldCancel = false;
		LogManager.getLogger().info("Starting compile...");
		int i = 0;
		for (CompileStep step : steps) {
			if (shouldCancel) {
				this.isActive = false;
				this.shouldCancel = false;
				return CompileResult.canceled();
			}
			
			if (listener != null) {
				listener.onCompileProgress((float) i / steps.size(), step.getDescription());
			}
			
			boolean success = false;
			try {
				success = step.execute(level, target, arguements, listener);
			} catch (Exception e) {
				LogManager.getLogger().error("Unable to complete step: "+step.getID(), e);
			}
			
			if (!success) {
				if (step.isRequired()) {
					this.isActive = false;
					return new CompileResult(CompileEndStatus.FAILED, "Unable to complete required step: "+step.getID()+". See console for details.");
				} else {
					listener.onError("Unable to complete step: "+step.getID()+". See console for details.");
				}
			}
			
			i++;
		}
		
		this.isActive = false;
		return CompileResult.successfulCompile();	
	}
	
	/**
	 * Get whether the compiler is actively compiling something.
	 */
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * If the compiler active, cancel the compilation.
	 * Doesn't cancel compilation steps; just the process as a whole.
	 */
	public void cancel() {
		this.shouldCancel = true;
	}
}
