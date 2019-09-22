package org.metaversemedia.scaffold.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Minecraft function, uncompiled.
 * @author Sam54123
 *
 */
public class MCFunction {
	/* All the commnands in the function */
	private List<String> commands = new ArrayList<String>();
	
	/**
	 * Compiler will replace all instances of key strings with associated value strings.
	 */
	private Map<String, String> variables = new HashMap<String, String>();
	
	/**
	 * Retrieves the list of commands, which is mutable
	 * @return Command list
	 */
	public List<String> commands() {
		return commands;
	}
	
	/**
	 * Compiler will replace all instances of key strings starting with $ with associated value strings.
	 * @return Variables map
	 */
	public Map<String, String> variables() {
		return variables;
	}
	
	/**
	 * Compile function to an mcfunction file
	 * @param compilePath File to compile to
	 * @throws IOException
	 */
	public void compile(File compilePath) throws IOException {
		
		// Make sure file exists
		if (!compilePath.exists()) {
			compilePath.getParentFile().mkdirs();
		}
		
		// Bake all variables
		List<String> compileCommands = new ArrayList<String>(commands);
		for (int c = 0; c < compileCommands.size(); c++) {
			for (String variable : variables.keySet()) {
				compileCommands.set(c, compileCommands.get(c).replace("$"+variable, variables.get(variable)));
			}
		}
		
		// Write to file
		BufferedWriter writer = new BufferedWriter(new FileWriter(compilePath));
		
		for (int i = 0; i< compileCommands.size(); i++) {
			writer.write(compileCommands.get(i));
			writer.newLine();
		}
		writer.close();

		
	}
	
	
	
	
	
}
