package org.scaffoldeditor.scaffold.entity.attribute;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class IntAttribute extends Attribute<Integer> {
	
	public static final String REGISTRY_NAME = "int_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<IntAttribute>() {

			@Override
			public IntAttribute create() {
				return new IntAttribute(0);
			}

			@Override
			public IntAttribute deserialize(Element element) {
				return new IntAttribute(Integer.valueOf(element.getAttribute("value")));
			}
		});
	}
	
	Integer value = 0;
	
	public IntAttribute(int value) {
		this.value = value;
		this.registryName = REGISTRY_NAME;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(registryName);
		element.setAttribute("value", value.toString());
		return element;
	}
	
	@Override
	public IntAttribute clone() {
		return this;
	}
}
