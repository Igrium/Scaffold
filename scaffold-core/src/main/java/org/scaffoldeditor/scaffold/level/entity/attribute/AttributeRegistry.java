package org.scaffoldeditor.scaffold.level.entity.attribute;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Element;

public final class AttributeRegistry {
	
	/**
	 * The registry attribute entity types.
	 * <br>
	 * (type name, attribute factory)
	 */
	@SuppressWarnings("rawtypes")
	public static final Map<String, AttributeFactory<? extends Attribute>> registry = new HashMap<>();
	
	/**
	 * Create an attribute.
	 * @param typeName Type of attribute to spawn.
	 * @return Newly created attribute.
	 */
	public static Attribute<?> createAttribute(String typeName) {
		if (!registry.containsKey(typeName)) {
			LogManager.getLogger().error("Unknown attribute type: "+typeName);
			return null;
		}
		
		Attribute<?> attribute = registry.get(typeName).create();
		attribute.registryName = typeName;
		return attribute;
	}
	
	/**
	 * Deserialize an attribute from XML.
	 * @param element Element to deserialize.
	 * @return Deserialized attribute
	 */
	public static Attribute<?> deserializeAttribute(Element element) {
		String typeName = element.getTagName();
		if (!registry.containsKey(typeName)) {
			LogManager.getLogger().error("Unknown attribute type: "+typeName);
			return null;
		}
		
		Attribute<?> attribute = registry.get(typeName).deserialize(element);
		attribute.registryName = typeName;
		return attribute;
	}
}
