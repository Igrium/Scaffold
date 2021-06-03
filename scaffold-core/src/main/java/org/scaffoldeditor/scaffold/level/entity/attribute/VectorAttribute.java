package org.scaffoldeditor.scaffold.level.entity.attribute;

import org.scaffoldeditor.scaffold.math.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VectorAttribute extends Attribute<Vector> {
	
	public static final String REGISTRY_NAME = "vector_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<VectorAttribute>() {

			@Override
			public VectorAttribute create() {
				return new VectorAttribute(new Vector(0, 0, 0));
			}

			@Override
			public VectorAttribute deserialize(Element element) {
				float x = Float.valueOf(element.getAttribute("x"));
				float y = Float.valueOf(element.getAttribute("y"));
				float z = Float.valueOf(element.getAttribute("z"));
				
				return new VectorAttribute(new Vector(x, y, z));
			}
		});
	}
	
	private Vector value;
	
	public VectorAttribute(Vector value) {
		this.value = value;
		this.registryName = REGISTRY_NAME;
	}

	@Override
	public Vector getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(registryName);
		element.setAttribute("x", String.valueOf(value.X()));
		element.setAttribute("y", String.valueOf(value.Y()));
		element.setAttribute("z", String.valueOf(value.Z()));
		return element;
	}
	
	@Override
	public VectorAttribute clone() {
		return this;
	}
}
