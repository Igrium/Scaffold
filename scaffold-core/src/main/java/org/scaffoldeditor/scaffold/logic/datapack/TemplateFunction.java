package org.scaffoldeditor.scaffold.logic.datapack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A function with various variables that can be applied at compile-time.
 * @author Igrium
 */
public class TemplateFunction extends AbstractFunction {
	/**
	 * The regex pattern to identify 
	 */
	public static final Pattern pattern = Pattern.compile("(\\${[^\\$]*})");
	
	/**
	 * The raw text of the function, before variables are applied.
	 */
	public final String rawText;
	private final Map<String, String> variables = new HashMap<>();
	
	/**
	 * Create a template functino object.
	 * @param namespace Namespace of the function.
	 * @param path Name of the function without the file extension.
	 * @param rawText Raw text of the function. Excpects a multi-line string.
	 */
	public TemplateFunction(String namespace, String path, String rawText) {
		super(namespace, path);
		this.rawText = rawText;
	}
	
	public void setVariable(String name, String value) {
		if (value == null) {
			variables.remove(name);
		} else {
			variables.put(name, value);
		}
	}
	
	public String getVariable(String name) {
		return variables.get(name);
	}
	
	public Set<String> getVariableNames() {
		return variables.keySet();
	}
	
	public void clear() {
		variables.clear();
	}
	
	@Override
	public List<Command> getCommands() {
		String[] lines = rawText.split(System.lineSeparator());
		return Arrays.asList(lines).stream().map(line -> parseLine(line)).collect(Collectors.toList());
	}
	
	private Command parseLine(String line) {
		String newString = "";
		int index = 0;
		
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			newString += line.substring(index, matcher.start());
			String varName = getVarName(line.substring(matcher.start(), matcher.end()));
			String value = variables.get(varName);
			if (value == null) {
				throw new IllegalStateException("Template function " + namespace + ":" + path
						+ " expects a variable by the name '" + varName + "', but none was provided!");
			}
			newString += value;
			index = matcher.end();
		}
		newString += line.substring(index);
		return Command.fromString(newString);
	}
	
	private String getVarName(String substring) {
		return substring.substring(2, substring.length()-1);
	}

}
