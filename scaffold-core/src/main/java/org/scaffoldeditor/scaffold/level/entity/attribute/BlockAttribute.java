package org.scaffoldeditor.scaffold.level.entity.attribute;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.scaffold.serialization.NBTSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.querz.nbt.tag.CompoundTag;

public class BlockAttribute extends Attribute<Block> {
	
	public static final String REGISTRY_NAME = "block_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<BlockAttribute>() {

			@Override
			public BlockAttribute create() {
				return new BlockAttribute(new Block("minecraft:stone"));
			}

			@Override
			public BlockAttribute deserialize(Element element) {
				String name = element.getAttribute("block_name");
				NodeList children = element.getElementsByTagName(NBTSerializer.TAG_NAME);
				CompoundTag properties = (CompoundTag) NBTSerializer.deserialize((Element) children.item(0));
				return new BlockAttribute(new Block(name, properties));
			}
		});
	}
	
	private final Block value;
	
	public BlockAttribute(Block value) {
		this.value = value;
	}

	@Override
	public Block getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(REGISTRY_NAME);
		element.setAttribute("block_name", value.getName());
		Element nbt = new NBTSerializer(value.getProperties()).serialize(document);
		element.appendChild(nbt);
		return element;
	}

	@Override
	public BlockAttribute clone() {
		return new BlockAttribute(new Block(value.getName(), value.getProperties().clone()));
	}

}
