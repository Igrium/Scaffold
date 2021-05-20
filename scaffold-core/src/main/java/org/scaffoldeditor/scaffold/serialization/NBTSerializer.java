package org.scaffoldeditor.scaffold.serialization;

import org.scaffoldeditor.nbt.NBTStrings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.github.mryurihi.tbnbt.tag.NBTTagCompound;

public class NBTSerializer implements XMLSerializable<NBTTagCompound> {
	
	public static final String TAG_NAME = "NBT";

	private final NBTTagCompound object;
	
	public NBTSerializer(NBTTagCompound nbt) {
		object = nbt;
	}
	
	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(TAG_NAME);
		Text text = document.createTextNode(NBTStrings.nbtToString(object));
		element.appendChild(text);
		return element;
	}
	
	public static NBTTagCompound deserialize(Element xml) {
		NodeList children = xml.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				Text text = (Text) child;
				return NBTStrings.nbtFromString(text.getData());
			}
		}
		
		throw new IllegalArgumentException("Passed NBT element does not contain a text node.");
	}
}
