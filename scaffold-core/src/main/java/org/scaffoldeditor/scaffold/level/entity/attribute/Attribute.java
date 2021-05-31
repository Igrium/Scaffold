package org.scaffoldeditor.scaffold.level.entity.attribute;

import org.scaffoldeditor.scaffold.serialization.XMLSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Attribute<T> implements XMLSerializable {
	/**
	 * The type name this attribute is serialized and deserialized with.
	 */
	public String registryName;
	
	public abstract T getValue();
	public abstract Element serialize(Document document);
	
	/**
	 * Create a copy of this attribute such that changes to mutable values
	 * are independant of the original.
	 * <br>
	 * Non-mutable attributes can simply return <code>this</code>
	 */
	public abstract Attribute<T> copy();
	
	@Override
	public String toString() {
		return getValue().toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Attribute && getValue().equals(((Attribute<?>) obj).getValue()));
	}
	
}
