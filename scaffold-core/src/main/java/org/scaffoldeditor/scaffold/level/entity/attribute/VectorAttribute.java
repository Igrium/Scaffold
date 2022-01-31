package org.scaffoldeditor.scaffold.level.entity.attribute;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VectorAttribute extends Attribute<Vector3dc> {
	
	public static final String REGISTRY_NAME = "vector_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<VectorAttribute>() {

			@Override
			public VectorAttribute create() {
				return new VectorAttribute(new Vector3d(0, 0, 0));
			}

			@Override
			public VectorAttribute deserialize(Element element) {
				double x = Double.valueOf(element.getAttribute("x"));
				double y = Double.valueOf(element.getAttribute("y"));
				double z = Double.valueOf(element.getAttribute("z"));
				
				return new VectorAttribute(new Vector3d(x, y, z));
			}
		});
	}
	
	private Vector3dc value;
	
	public VectorAttribute(Vector3dc value) {
		this.value = value;
		this.registryName = REGISTRY_NAME;
	}
	
	public VectorAttribute(Vector3ic value) {
		this(new Vector3d(value));
	}

	public VectorAttribute(Vector3fc value) {
		this(new Vector3d(value));
	}

	public VectorAttribute(double x, double y, double z) {
		this(new Vector3d(x, y, z));
	}

	public VectorAttribute() {
		this(new Vector3d());
	}

	@Override
	public Vector3dc getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(registryName);
		element.setAttribute("x", String.valueOf(value.x()));
		element.setAttribute("y", String.valueOf(value.y()));
		element.setAttribute("z", String.valueOf(value.z()));
		return element;
	}
	
	@Override
	public VectorAttribute clone() {
		return this;
	}
}
