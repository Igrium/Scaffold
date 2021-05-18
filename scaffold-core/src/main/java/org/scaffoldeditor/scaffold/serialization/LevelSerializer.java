package org.scaffoldeditor.scaffold.serialization;

import org.scaffoldeditor.scaffold.level.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class LevelSerializer implements XMLSerializable<Level> {

	@Override
	public Node serialize(Level level, Document document) {
		Element element = document.createElement("Level");
		element.setAttribute("pretty_name", level.getPrettyName());
		
		return null;
	}

}
