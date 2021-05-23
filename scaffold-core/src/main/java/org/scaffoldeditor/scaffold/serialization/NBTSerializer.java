package org.scaffoldeditor.scaffold.serialization;

import java.io.IOException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

public class NBTSerializer implements XMLSerializable<Tag<?>> {
	
	public static final String TAG_NAME = "NBT";

	private final CompoundTag object;
	
	public NBTSerializer(CompoundTag nbt) {
		object = nbt;
	}
	
	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(TAG_NAME);
		Text text;
		try {
			text = document.createTextNode(SNBTUtil.toSNBT(object));
			element.appendChild(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return element;
	}
	
	public static Tag<?> deserialize(Element xml) {
		NodeList children = xml.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				Text text = (Text) child;
				try {
					return SNBTUtil.fromSNBT(text.getData());
				} catch (DOMException | IOException e) {
					e.printStackTrace();
					return new CompoundTag();
				}
			}
		}
		
		throw new IllegalArgumentException("Passed NBT element does not contain a text node.");
	}
}
