package org.scaffoldeditor.scaffold.entity.attribute;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

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

	/**
	 * Parse an attribute of this type from a string reader. Used in the Scaffold
	 * CLI.
	 * 
	 * @param reader String reader to parse from.
	 * @throws CommandSyntaxException        If there's an error while parsing the
	 *                                       string.
	 * @throws UnsupportedOperationException If this attribute can't be parsed from
	 *                                       a string.
	 */
	default T parse(StringReader reader) throws CommandSyntaxException, UnsupportedOperationException {
		throw new UnsupportedOperationException("Unable to parse this type of attribute from a string.");
	}
}
