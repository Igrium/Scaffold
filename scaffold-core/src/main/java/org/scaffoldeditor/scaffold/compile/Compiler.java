package org.scaffoldeditor.scaffold.compile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;

public class Compiler {
	
	public static class CompileResult {
		public final boolean success;
		public final String errorMessage;
		
		public CompileResult(boolean success, String errorMessage) {
			this.success = success;
			this.errorMessage = errorMessage;
		}
		
		public static CompileResult successfulCompile() {
			return new CompileResult(true, "");
		}
	}
	
	public static interface CompileProgressListener {	
		/**
		 * Called when a new step is started.
		 * @param percent Percentage of the compile that's complete.
		 * @param description Text to show in the UI.
		 */
		void onCompileProgress(float percent, String description);
		
		void onError(String description);
	}
	
	public static Compiler getDefault() {
		Compiler compiler = new Compiler();
		
		compiler.steps.add(new SetupStep());
		compiler.steps.add(new CompileWorldStep());
		compiler.steps.add(new WriteWorldStep());
		compiler.steps.add(new CompileLogicStep());
		compiler.steps.add(new CompileResourcepackStep());
		
		return compiler;
	}
	
	/**
	 * The compile steps this compiler will execute, in order.
	 */
	public final List<CompileStep> steps = new ArrayList<>();
	
	/**
	 * Compile a level.
	 * @param level Level to compile.
	 * @param target Folder to compile into (the world folder itself).
	 * @param arguements Compile arguements.
	 * @param listener Listener to update the UI.
	 * @return The result of the compile.
	 */
	public CompileResult compile(Level level, Path target, Map<String, Attribute<?>> arguements, CompileProgressListener listener) {
		System.out.println("Starting compile...");
		int i = 0;
		for (CompileStep step : steps) {
			if (listener != null) {
				listener.onCompileProgress((float) i / steps.size(), step.getDescription());
			}
			
			boolean success = false;
			try {
				success = step.execute(level, target, arguements, listener);
			} catch (Exception e) {}
			
			if (!success) {
				if (step.isRequired()) {
					return new CompileResult(false, "Unable to complete required step: "+step.getID()+". See console for details.");
				} else {
					listener.onError("Unable to complete step: "+step.getID());
				}
			}
			
			i++;
		}
		
		return CompileResult.successfulCompile();	
	}
}
