package org.scaffoldeditor.scaffold.level.entity.attribute;

import org.w3c.dom.Element;

public interface AttributeFactory<T extends Attribute<?>> {
	
	/**
	 * Create an instance of the attribute.
	 * @return Newly created attribute.
	 */
	public T create();
	
	/**
	 * Deserialize an attribute of this type from XML.
	 * @param element XML element.
	 * @return Deserialized attribute.
	 */
	public T deserialize(Element element);
}
