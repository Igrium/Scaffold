package org.scaffoldeditor.scaffold.entity.attribute;

import org.scaffoldeditor.scaffold.operation.ChangeAttributesOperation;
import org.scaffoldeditor.scaffold.serialization.XMLSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Attribute<T> implements XMLSerializable, Cloneable {
	/**
	 * The type name this attribute is serialized and deserialized with.
	 */
	public String registryName;

	/**
	 * Get the value this attribute represents. <br>
	 * <b>Note:</b> Mutable values break the undo/redo system. If this attribute is
	 * mutable, make a copy of it to edit and then insert the edited version with a
	 * {@link ChangeAttributesOperation}.
	 */
	public abstract T getValue();

	public abstract Element serialize(Document document);
	
	/**
	 * Create a copy of this attribute such that changes to mutable values
	 * are independant of the original.
	 * <br>
	 * Non-mutable attributes can simply return <code>this</code>
	 */
	public abstract Attribute<T> clone();
	
	@Override
	public String toString() {
		return getValue().toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Attribute && getValue().equals(((Attribute<?>) obj).getValue()));
	}
	
}
