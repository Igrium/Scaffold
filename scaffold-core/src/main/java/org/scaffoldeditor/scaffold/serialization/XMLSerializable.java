package org.scaffoldeditor.scaffold.serialization;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A class that can be serialized to XML. 
 * @author Igrium
 */
public interface XMLSerializable<T> {
	
	/**
	 * Serialize this object into XML.
	 * @param document The document to serialize onto.
	 * @return Serialized XML node.
	 */
	public Node serialize(Document document);
}
