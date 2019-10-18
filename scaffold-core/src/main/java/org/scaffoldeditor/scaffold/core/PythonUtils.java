package org.scaffoldeditor.scaffold.core;

import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**
 * This class simplifies interfacing with Jython.
 * @author Sam54123
 *
 */
public final class PythonUtils {
	/* Main python interpreter */
	private static PythonInterpreter interpreter;
	
	/**
	 * Gets whether the Python system is initialized
	 * @return Is initialized?
	 */
	public static boolean isInitialized() {
		return interpreter != null;
	}
	
	/**
	 * Initialize the python interpreter
	 */
	public static void init() {
		if (!isInitialized()) {
			System.setProperty("python.console.encoding", "UTF-8");
			System.setProperty("python.import.site", "false");
			PythonInterpreter.initialize(System.getProperties(), System.getProperties(), new String[0]);
			interpreter = new PythonInterpreter();
			interpreter.setOut(System.out);
		}
	}
	
	/**
	 * Get the Python interpriter and initialize if not initialized.
	 * @return Interpreter
	 */
	public static PythonInterpreter getInterpreter() {
		if (!isInitialized()) {
			init();
		}
		
		return interpreter;
	}
	
	/**
	 * Get the Python system state.
	 * @return System state
	 */
	public static PySystemState getSystemState() {
		if (!isInitialized()) {
			init();
		}
		
		return interpreter.getSystemState();
	}
	
	public static void setArgs(String[] args) {
		getSystemState().argv.clear();
		for (String arg : args) {
			getSystemState().argv.append(new PyString(arg));
		}
	}
}
