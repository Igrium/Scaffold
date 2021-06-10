package org.scaffoldeditor.scaffold.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

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
	 * @param script    Source code of script.
	 * @param directory Directory to run in.
	 * @param args      Arguements to send to Python. Note: their index will be
	 *                  offset by 1, as the first arguement is always
	 *                  <code>-c</code>.
	 * @return Exit code of the script.
	 * @throws IOException If an IO exception occurs trying to run the script.
	 */
	public static int runScript(String script, File directory, String ...args) throws IOException {
		if (!isPythonInstalled()) {
			throw new IOException("Unable to run script because Python is not installed!");
		}
		
		List<String> params = new ArrayList<String>();
		params.add("python");
		params.add("-c");
		params.add(script);
		params.addAll(Arrays.asList(args));
		ProcessBuilder pb = new ProcessBuilder(params).directory(directory).inheritIO();
		
		Process p = pb.start();
		try {
			return p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			p.destroy();
			return p.exitValue();
		}
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
