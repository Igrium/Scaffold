package org.scaffoldeditor.scaffold.level.entity.attribute;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VectorAttribute extends Attribute<Vector3f> {
	
	public static final String REGISTRY_NAME = "vector_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<VectorAttribute>() {

			@Override
			public VectorAttribute create() {
				return new VectorAttribute(new Vector3f(0, 0, 0));
			}

			@Override
			public VectorAttribute deserialize(Element element) {
				float x = Float.valueOf(element.getAttribute("x"));
				float y = Float.valueOf(element.getAttribute("y"));
				float z = Float.valueOf(element.getAttribute("z"));
				
				return new VectorAttribute(new Vector3f(x, y, z));
			}
		});
	}
	
	private Vector3f value;
	
	public VectorAttribute(Vector3f value) {
		this.value = value;
		this.registryName = REGISTRY_NAME;
	}
	
	public VectorAttribute(Vector3i value) {
		this(value.toFloat());
	}

	@Override
	public Vector3f getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(registryName);
		element.setAttribute("x", String.valueOf(value.x));
		element.setAttribute("y", String.valueOf(value.y));
		element.setAttribute("z", String.valueOf(value.z));
		return element;
	}
	
	@Override
	public VectorAttribute clone() {
		return this;
	}
}
