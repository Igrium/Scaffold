package org.scaffoldeditor.scaffold.level.entity.attribute;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FloatAttribute extends Attribute<Float> {
	
	public static final String REGISTRY_NAME = "float_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<FloatAttribute>() {

			@Override
			public FloatAttribute create() {
				return new FloatAttribute(0);
			}

			@Override
			public FloatAttribute deserialize(Element element) {
				return new FloatAttribute(Float.valueOf(element.getAttribute("value")));
			}
		});
	}
	
	private Float value;
	
	public FloatAttribute(float value) {
		this.value = value;
		this.registryName = REGISTRY_NAME;
	}

	@Override
	public Float getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(registryName);
		element.setAttribute("value", value.toString());
		return element;
	}
	
	@Override
	public FloatAttribute clone() {
		return this;
	}
}
