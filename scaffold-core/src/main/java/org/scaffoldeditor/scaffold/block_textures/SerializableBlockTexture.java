package org.scaffoldeditor.scaffold.block_textures;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.serialization.XMLSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents a block texture that can be serialized into XML. Like entities,
 * serializable block textures rely on the attribute system to save their
 * parameters.
 * 
 * @author Igrium
 */
public abstract class SerializableBlockTexture implements BlockTexture, XMLSerializable {
	protected final Map<String, Attribute<?>> attributes = new HashMap<>();
	
	public static SerializableBlockTexture DEFAULT = new SingleBlockTexture(new Block("minecraft:stone"));
	
	/**
	 * Get the name this block texture should use in its registry.
	 */
	public abstract String getRegistryName();
	
	public Attribute<?> getAttribute(String name) {
		return attributes.get(name);
	}
	
	public void setAttribute(String name, Attribute<?> value) {
		attributes.put(name, value);
	}
	
	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(getRegistryName());
		
		for (String name : this.attributes.keySet()) {
			Element attribute = this.attributes.get(name).serialize(document);
			attribute.setAttribute("name", name);
			element.appendChild(attribute);
		}
		
		return element;
	}
	
	public SerializableBlockTexture copy() {
		SerializableBlockTexture other = BlockTextureRegistry.createBlockTexture(getRegistryName());
		for (String name : attributes.keySet()) {
			other.attributes.put(name, attributes.get(name));
		}
		return other;
	}
}
