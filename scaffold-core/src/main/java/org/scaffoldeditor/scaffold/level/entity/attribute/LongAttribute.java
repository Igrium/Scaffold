package org.scaffoldeditor.scaffold.level.entity.attribute;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LongAttribute extends Attribute<Long> {
	
	public static final String REGISTRY_NAME = "long_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<LongAttribute>() {

			@Override
			public LongAttribute create() {
				return new LongAttribute(0);
			}

			@Override
			public LongAttribute deserialize(Element element) {
				return new LongAttribute(Long.valueOf(element.getAttribute("value")));
			}
		});
	}
	
	private Long value;
	
	public LongAttribute(long value) {
		this.value = value;
		this.registryName = REGISTRY_NAME;
	}

	@Override
	public Long getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(registryName);
		element.setAttribute("value", value.toString());
		return element;
	}
	
	@Override
	public LongAttribute copy() {
		return this;
	}
}
