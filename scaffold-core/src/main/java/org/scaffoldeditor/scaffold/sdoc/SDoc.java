package org.scaffoldeditor.scaffold.sdoc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.io.AssetManager;

/**
 * Represents the documentation of an entity. Includes descriptions and pretty names.
 * @author Igrium
 */
public class SDoc {
	private String description;
	public final Map<String, ComponentDoc> attributes = new HashMap<>();
	public final Map<String, ComponentDoc> inputs = new HashMap<>();
	public final Map<String, ComponentDoc> outputs = new HashMap<>();
	
	public SDoc(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String write() {
		String output = description + System.lineSeparator();
		for (ComponentDoc comp : attributes.values()) {
			output += writeComponent(comp);
		}
		for (ComponentDoc comp : inputs.values()) {
			output += writeComponent(comp);
		}
		for (ComponentDoc comp : outputs.values()) {
			output += writeComponent(comp);
		}
		return output.strip();
	}
	
	private String writeComponent(ComponentDoc component) {
		return "@"+component.write()+System.lineSeparator();
	}
	
	public static SDoc parse(String input) {
		int descriptionEnd = input.indexOf('@');
		if (descriptionEnd == -1) {
			return new SDoc(input.strip());
		}
		
		String description = input.substring(0, descriptionEnd);
		description = description.strip();
		
		SDoc doc = new SDoc(description);
		
		String[] components = input.substring(descriptionEnd).split("@");
		for (String str : components) {
			if (str.length() == 0) continue;	
			ComponentDoc component = ComponentDoc.parse(str);
			
			if (component.getType().equals("attribute")) {
				doc.attributes.put(component.getName(), component);
			} else if (component.getType().equals("input")) {
				doc.inputs.put(component.getName(), component);
			} else if (component.getType().equals("output")) {
				doc.outputs.put(component.getName(), component);
			} else {
				LogManager.getLogger().error("Unknown component type: "+component);
			}
		}
		
		return doc;
	}
	
	public static SDoc loadAsset(AssetManager assetManager, String asset) {
		if (!(assetManager.getLoader(asset).isAssignableTo(SDoc.class))) {
			throw new IllegalArgumentException("Unable to load SDoc from asset "+asset+" because it has the wrong file extension!");
		}
		
		try {
			return (SDoc) assetManager.loadAsset(asset, false);
		} catch (IOException e) {
			LogManager.getLogger().error("Error loading Scaffold documentation!", e);
			return new SDoc("[error loading documentation]");
		}
	}
}
