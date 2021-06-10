package org.scaffoldeditor.scaffold.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.core.Project;

/**
 * A utility function for executing Python code.
 * @author Igrium
 */
public final class PythonUtils {
	private PythonUtils() {}
	
	public static final String PRE_COMPILE_SCRIPT;
	public static final String POST_COMPILE_SCRIPT;
	
	public static boolean isPythonInstalled() {
		try {
			Process send = Runtime.getRuntime().exec("python --version");
			BufferedReader reader = new BufferedReader(new InputStreamReader(send.getInputStream()));
			return (reader.readLine().startsWith("Python"));
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Runs a Python script, causing this thread to hold untill it's complete.
	 * 
	 * @param script     Source code of script.
	 * @param directory  Directory to run in.
	 * @param onReadLine A function that gets called whenever the script outputs
	 *                   text to the console (like during a <code>print()</code>
	 *                   statement.)
	 * @param args       Arguements to send to Python. Note: their index will be
	 *                   offset by 1, as the first arguement is always
	 *                   <code>-c</code>.
	 * @return Exit code of the script.
	 * @throws IOException If an IO exception occurs trying to run the script.
	 */
	public static int runScript(String script, File directory, Consumer<String> onReadLine, String ...args) throws IOException {
		if (!isPythonInstalled()) {
			throw new IOException("Unable to run script because Python is not installed!");
		}
		
		// For some reason, processbuilder freaks out with quotes. We need to escape them.
		script = script.replace("\"", "\\\"");
		
		List<String> params = new ArrayList<String>();
		params.add("python");
		params.add("-c");
		params.add(script);
		params.addAll(Arrays.asList(args));
		ProcessBuilder pb = new ProcessBuilder(params).directory(directory);
		
		Process process = pb.start();
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			onReadLine.accept(s);
		}
		
		StringWriter writer = new StringWriter();
		stdError.transferTo(writer);
		String error = writer.toString();
		
		if (error.length() > 0) {
			LogManager.getLogger().error("Error executing Python script" + System.lineSeparator() + error);
		}
		
		try {
			return process.waitFor();
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
	}
	
	/**
	 * Runs a Python script, causing this thread to hold untill it's complete.
	 * 
	 * @param script    Source code of script.
	 * @param directory Directory to run in.
	 * @param args      Arguements to send to Python. Note: their index will be
	 *                  offset by 1, as the first arguement is always
	 *                  <code>-c</code>.
	 * @return Exit code of the script.
	 * @throws IOException If an IO exception occurs trying to run the script.
	 */
	public static int runScript(String script, File directory, String ...args) throws IOException {
		return runScript(script, directory, (string) -> {
			LogManager.getLogger().info("[python] "+string);
		}, args);
	}
	
	/**
	 * Locate a script file by its name.
	 * 
	 * @param project    Project to look in.
	 * @param scriptName Name of script. If it doesn't end in <code>.py</code> it
	 *                   will automatically be appended.
	 * @return Absolute script file.
	 */
	public static File resolveScript(Project project, String scriptName) {
		if (!scriptName.endsWith(".py")) scriptName = scriptName+".py";
		return project.getProjectFolder().resolve("scripts").resolve(scriptName).toFile();
	}

	
	static {
		String preCompile;
		try {
			preCompile = IOUtils.toString(PythonUtils.class.getResourceAsStream("/scripts/pre_compile.py"), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			preCompile = "";
		}
		PRE_COMPILE_SCRIPT = preCompile;
		String postCompile;
		try {
			postCompile = IOUtils.toString(PythonUtils.class.getResourceAsStream("/scripts/post_compile.py"), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			postCompile = "";
		}
		POST_COMPILE_SCRIPT = postCompile;
	}
}
