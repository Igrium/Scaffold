package org.scaffoldeditor.scaffold.serialization;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.AttributeRegistry;
import org.scaffoldeditor.scaffold.math.Vector;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Serializes Scaffold entities to XML.
 * @author Igrium
 */
public class EntitySerializer implements XMLSerializable<Entity> {
	
	private Entity entity;
	
	public EntitySerializer(Entity entity) {
		this.entity = entity;
	}

	@Override
	/**
	 * Serialize an entity into XML.
	 */
	public Element serialize(Document document) {
		Element root = document.createElement(entity.registryName);
		root.setAttribute("name", entity.getName());

		Element attributes = document.createElement("attributes");
		for (String name : entity.getAttributes()) {
			try {
				Element attribute = entity.getAttribute(name).serialize(document);
				attribute.setAttribute("name", name);
				attributes.appendChild(attribute);	
			} catch (DOMException e) {
				e.printStackTrace();
			}

		}
		
		root.appendChild(attributes);
		
		return root;
	}
	
	/**
	 * Load an entity without adding it to the level.
	 * @param xml XML element to load from.
	 * @param level Level to tell the entity it belongs to.
	 * @return Loaded entity.
	 */
	public static Entity loadEntity(Element xml, Level level) {
		String typeName = xml.getTagName();
		String name = xml.getAttribute("name");	
		Entity entity = EntityRegistry.createEntity(typeName, level, name, true);
		NodeList children = xml.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) child;
				if (element.getTagName().equals(Vector.REGISTRY_NAME)) {
					entity.setPosition(Vector.deserialize(element));
				} else if (element.getTagName().equals("attributes")) {
					loadAttributes(element, entity);
				}
			}
		}
		entity.onUnserialized(xml);
		entity.onUpdateAttributes(true);
		return entity;	
	}
	
	/**
	 * Load an entity from XML and add it to the level.
	 * @param xml XML element to load from.
	 * @param level Level to load into.
	 * @return Loaded entity.
	 */
	public static Entity deserialize(Element xml, Level level) {
		Entity entity = loadEntity(xml, level);
		
		level.addEntity(entity, level.getEntityStack().size(), true);
		
		return entity;
	}
	
	private static void loadAttributes(Element xml, Entity entity) {
		NodeList children = xml.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) child;
				Attribute<?> attribute = AttributeRegistry.deserializeAttribute(element);
				entity.setAttribute(element.getAttribute("name"), attribute, true);
			}
		}
	}
}
