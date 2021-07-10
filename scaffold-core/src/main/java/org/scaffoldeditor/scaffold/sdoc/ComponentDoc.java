package org.scaffoldeditor.scaffold.sdoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComponentDoc {
	private String type;
	private String name;
	private String prettyName;
	private String description;
	
	public ComponentDoc(String type, String name, String prettyName, String description) {
		this.type = type;
		this.name = name;
		this.prettyName = prettyName;
		this.description = description;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPrettyName() {
		return prettyName;
	}
	
	public void setPrettyName(String prettyName) {
		this.prettyName = prettyName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String write() {
		return String.join(" ", type, name, "\""+prettyName+"\"", description);
	}
	
	/**
	 * Parse a ComponentDoc from a line.
	 * @param line Input string in the format {@code type name "Pretty Name" Single line description.}
	 * @return Parsed object.
	 */
	public static ComponentDoc parse(String line) {
		String[] args = split(line);
		try {
			String type = args[0];
			String name = args[1];
			String prettyName = args[2];
			String description = "";
			if (args.length > 3) {
				description = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
			}
			return new ComponentDoc(type, name, prettyName, description);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Unfinished component doc: " + line + " <--");
		}
	}
	
	
	private static String[] split(String in) {
		List<String> tokens = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		
		boolean insideQuote = false;
		
		for (char c : in.strip().toCharArray()) {
			if (c == '"') {
				insideQuote = !insideQuote;
			} else if (c == ' ' && !insideQuote) {
				tokens.add(sb.toString());
				sb.delete(0, sb.length());
			} else {
				sb.append(c);
			}
		}
		
		tokens.add(sb.toString());
		
		return tokens.toArray(new String[0]);
	}
	
	@Override
	public ComponentDoc clone(){
		return new ComponentDoc(type, name, prettyName, description);
	}
	
	public static boolean containsName(Iterable<ComponentDoc> collection, String name) {
		for (ComponentDoc doc : collection) {
			if (name.equals(doc.name)) return true;
		}
		return false;
	}
}
