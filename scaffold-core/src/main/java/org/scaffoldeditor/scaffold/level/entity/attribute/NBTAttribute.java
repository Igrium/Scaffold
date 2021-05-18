package org.scaffoldeditor.scaffold.level.entity.attribute;

import java.util.HashMap;

import org.scaffoldeditor.scaffold.serialization.NBTSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.mryurihi.tbnbt.tag.NBTTagCompound;

public class NBTAttribute extends Attribute<NBTTagCompound> {
	
	public static final String REGISTRY_NAME = "nbt_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<NBTAttribute>() {

			@Override
			public NBTAttribute create() {
				return new NBTAttribute(new NBTTagCompound(new HashMap<>()));
			}

			@Override
			public NBTAttribute deserialize(Element element) {
				NodeList children = element.getElementsByTagName(NBTSerializer.TAG_NAME);
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						Element nbtElement = (Element) child;
						return new NBTAttribute(NBTSerializer.deserialize(nbtElement));
					}
				}
				throw new IllegalArgumentException("Improperly formatted NBT attribute!");
			}
			
		});
	}
	
	private NBTTagCompound value;
	
	public NBTAttribute(NBTTagCompound value) {
		this.value = value;
	}
	
	@Override
	public NBTTagCompound getValue() {
		return value;
	}

	@Override
	public void setValue(NBTTagCompound value) {
		this.value = value;
	}

	@Override
	public Element serialize(Document document) {
		Element base = document.createElement(registryName);
		Element nbt = new NBTSerializer(value).serialize(document);
		base.appendChild(nbt);
		return base;
	}

}
