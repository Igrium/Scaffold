package org.scaffoldeditor.scaffold.logic.datapack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.io.AssetManager;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;

/**
 * A function with various variables that can be applied at compile-time.
 * @author Igrium
 */
public class TemplateFunction extends AbstractFunction {
	/**
	 * The regex pattern to identify 
	 */
	public static final Pattern pattern = Pattern.compile("(\\$\\{[^\\$\\{\\}]*\\})");
	
	/**
	 * The raw text of the function, before variables are applied.
	 */
	public final String rawText;
	private final Map<String, String> variables = new HashMap<>();
	
	/**
	 * Create a template functinon object.
	 * @param identifier Function identifier.
	 * @param rawText Raw text of the function. Excpects a multi-line string.
	 */
	public TemplateFunction(Identifier identifier, String rawText) {
		super(identifier);
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
				throw new IllegalStateException("Template function " + this
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
	
	@Override
	public TemplateFunction clone() {
		return cloneWithID(id);
	}
	
	public TemplateFunction cloneWithID(Identifier id) {
		TemplateFunction cloned = new TemplateFunction(id, rawText);
		cloned.variables.putAll(variables);
		return cloned;
	}
	
	public static TemplateFunction fromInputStream(Identifier id, InputStream in) {
		String raw = new BufferedReader(new InputStreamReader(in)).lines()
				.collect(Collectors.joining(System.lineSeparator()));
		return new TemplateFunction(id, raw);
	}
	
	/**
	 * Load a template function from an asset.
	 * @param manager Asset manager to use.
	 * @param id Identifier to assign to loaded function.
	 * @param assetPath Asset to load.
	 * @return Modifiable template function.
	 * @throws IOException If an IO Exception occurs while loading.
	 */
	public static TemplateFunction fromAsset(AssetManager manager, Identifier id, String assetPath) throws IOException {
		if (!manager.getLoader(assetPath).isAssignableTo(TemplateFunction.class)) {
			throw new IOException(assetPath+" is not parsable as a function!");
		}
		
		TemplateFunction loaded = (TemplateFunction) manager.loadAsset(assetPath, false);
		return loaded.cloneWithID(id);
	}
}
