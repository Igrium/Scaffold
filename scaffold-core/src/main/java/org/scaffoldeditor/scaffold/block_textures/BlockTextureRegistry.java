package org.scaffoldeditor.scaffold.block_textures;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.level.entity.attribute.AttributeRegistry;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class BlockTextureRegistry {
	private BlockTextureRegistry() {}
	
	public static final Map<String, BlockTextureFactory<?>> registry = new HashMap<>();
	
	public static SerializableBlockTexture createBlockTexture(String registryName) {
		if (!registry.containsKey(registryName)) {
			LogManager.getLogger().error("Unknown block texture type: " + registryName);
			return SerializableBlockTexture.DEFAULT;
		}
		return registry.get(registryName).create();
	}
	
	public static SerializableBlockTexture deserializeBlockTexture(Element element) {
		String registryName = element.getTagName();
		
		SerializableBlockTexture texture = createBlockTexture(registryName);
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element attribute = (Element) child;
				texture.attributes.put(attribute.getAttribute("name"), AttributeRegistry.deserializeAttribute(attribute));
			}
		}
		return texture;
	}
}
