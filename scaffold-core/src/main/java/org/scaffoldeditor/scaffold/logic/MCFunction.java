package org.scaffoldeditor.scaffold.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
	
	/* This function's name. */
	private String name;
	
	public MCFunction(String name) {
		setName(name);
	}
	
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
	 * Get's this function's name.
	 * @return Name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set this function's name.
	 * @param name New name
	 */
	public void setName(String name) {
		this.name = name.replaceAll("\\s+","").toLowerCase();
		variables.put("functionname", this.name);
	}
	
	/**
	 * Add a command to the function.
	 * @param command Command
	 */
	public void addCommand(String command) {
		commands.add(command);
	}
	
	
	/**
	 * Add or set a variable in the function
	 * @param name Variable name
	 * @param value Variable value
	 */
	public void setVariable(String name, String value) {
		variables.put(name, value);
	}
	
	/**
	 * Get a variable by name
	 * @param name Variable name
	 * @return Variable value (null if nonexistant.)
	 */
	public String getVariable(String name) {
		return variables.get(name);
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
