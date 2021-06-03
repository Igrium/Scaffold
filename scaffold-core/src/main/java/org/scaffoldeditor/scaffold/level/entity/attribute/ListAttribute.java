package org.scaffoldeditor.scaffold.level.entity.attribute;

import java.util.ArrayList;
import java.util.List;

import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ListAttribute extends Attribute<List<? extends Attribute<?>>> {
	
	public static final String REGISTRY_NAME = "list_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<Attribute<?>>() {

			@Override
			public Attribute<?> create() {
				return new ListAttribute(new ArrayList<>());
			}

			@Override
			public Attribute<?> deserialize(Element element) {
				List<Attribute<?>> attributes = new ArrayList<>();
				NodeList children = element.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						attributes.add(AttributeRegistry.deserializeAttribute((Element) child));
					}
				}
				
				return new ListAttribute(attributes);
			}
		});
	}
	
	private final List<? extends Attribute<?>> value;
	
	public ListAttribute(List<? extends Attribute<?>> value) {
		this.value = value;
	}

	/**
	 * Get the list behind this list attribute. <br>
	 * {@link Entity#onUpdateAttributes} should be called after updating if an
	 * recompilation is desired.
	 */
	public List<? extends Attribute<?>> getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(registryName);
		for (Attribute<?> att : value) {
			element.appendChild(att.serialize(document));
		}
		
		return element;
	}
	
	@Override
	public ListAttribute clone() {
		List<Attribute<?>> newList = new ArrayList<>();
		for (Attribute<?> att : value) {
			newList.add(att.clone());
		}
		
		return new ListAttribute(newList);
	}

}
