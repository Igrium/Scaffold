package org.scaffoldeditor.scaffold.entity.attribute;

import java.io.IOException;

import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.serialization.NBTSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;

public class NBTAttribute extends Attribute<CompoundTag> {
	
	public static final String REGISTRY_NAME = "nbt_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<NBTAttribute>() {

			@Override
			public NBTAttribute create() {
				return new NBTAttribute(new CompoundTag());
			}

			@Override
			public NBTAttribute deserialize(Element element) {
				NodeList children = element.getElementsByTagName(NBTSerializer.TAG_NAME);
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						Element nbtElement = (Element) child;
						return new NBTAttribute((CompoundTag) NBTSerializer.deserialize(nbtElement));
					}
				}
				throw new IllegalArgumentException("Improperly formatted NBT attribute!");
			}
			
		});
	}
	
	private CompoundTag value;
	
	public NBTAttribute(CompoundTag value) {
		this.value = value;
		this.registryName = REGISTRY_NAME;
	}

	public NBTAttribute() {
		this(new CompoundTag());
	}
	
	/**
	 * Get the compound tag backing this attribute. <br>
	 * {@link Entity#onUpdateAttributes} should be called after updating if a
	 * recompilation is desired.
	 */
	public CompoundTag getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element base = document.createElement(registryName);
		Element nbt = new NBTSerializer(value).serialize(document);
		base.appendChild(nbt);
		return base;
	}
	
	@Override
	public NBTAttribute clone() {
		NBTAttribute update = new NBTAttribute(value.clone());
		update.registryName = this.registryName;
		return update;
	}

	@Override
	public String toString() {
		try {
			return SNBTUtil.toSNBT(getValue());
		} catch (IOException e) {
			throw new RuntimeException("Error generating SNBT!", e);
		}
	}
}
