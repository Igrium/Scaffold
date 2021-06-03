package org.scaffoldeditor.scaffold.level.entity.attribute;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BooleanAttribute extends Attribute<Boolean> {
	
	public static final String REGISTRY_NAME = "bool_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<BooleanAttribute>() {

			@Override
			public BooleanAttribute create() {
				return new BooleanAttribute(false);
			}

			@Override
			public BooleanAttribute deserialize(Element element) {
				return new BooleanAttribute(Boolean.valueOf(element.getAttribute("value")));
			}
		});
	}
	
	private Boolean value;
	
	public BooleanAttribute(boolean value) {
		this.value = value;
		this.registryName = REGISTRY_NAME;
	}
	
	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(registryName);
		element.setAttribute("value", value.toString());
		return element;
	}
	
	@Override
	public BooleanAttribute clone() {
		return this;
	}
}
