package org.scaffoldeditor.scaffold.compile;

import java.nio.file.Path;
import java.util.Map;

import org.scaffoldeditor.scaffold.compile.Compiler.CompileProgressListener;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;

/**
 * A single step of the compilation process (ex. compiling entity logic)
 * @author Igrium
 */
public interface CompileStep {
	

	
	/**
	 * Execute this compilation step.
	 * @param level Level to compile.
	 * @param target Target world directory.
	 * @param args Additional arguements.
	 * @param listener Progress listener.
	 * @return Success.
	 */
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args, CompileProgressListener listener);

	
	/**
	 * Get whether this step failing should cause the entire compilation to fail.
	 */
	public boolean isRequired();
	
	/**
	 * Get the text that should be displayed in the UI at this step.
	 */
	public String getDescription();
	
	/**
	 * The ID of this step. Used for locating it in the compile stack.
	 */
	public String getID();
}
