package org.scaffoldeditor.scaffold.level.entity.attribute;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DoubleAttribute extends Attribute<Double> {
	
	public static final String REGISTRY_NAME = "double_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<DoubleAttribute>() {

			@Override
			public DoubleAttribute create() {
				return new DoubleAttribute(0);
			}

			@Override
			public DoubleAttribute deserialize(Element element) {
				return new DoubleAttribute(Double.valueOf(element.getAttribute("value")));
			}
		});
	}

	private Double value;
	
	public DoubleAttribute(double value) {
		this.value = value;
	}
	
	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(registryName);
		element.setAttribute("value", value.toString());
		return element;
	}

}
