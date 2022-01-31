package org.scaffoldeditor.scaffold.entity.attribute;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StringAttribute extends Attribute<String> {
	public static final String REGISTRY_NAME = "string_attribute";
	
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<StringAttribute>() {
			
			@Override
			public StringAttribute deserialize(Element element) {
				return new StringAttribute(element.getAttribute("value"));
			}
			
			@Override
			public StringAttribute create() {
				return new StringAttribute("");
			}
		});
	}
	
	private String value;
	
	public StringAttribute(String value) {
		this.registryName = REGISTRY_NAME;
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(registryName);
		element.setAttribute("value", value);
		return element;
	}
	
	@Override
	public StringAttribute clone() {
		return this;
	}
}
