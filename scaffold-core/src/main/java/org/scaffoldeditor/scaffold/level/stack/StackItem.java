package org.scaffoldeditor.scaffold.level.stack;

import java.util.Objects;

import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.serialization.EntitySerializer;
import org.scaffoldeditor.scaffold.serialization.XMLSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents a single item on the level stack.
 * @author Igrium
 */
public class StackItem implements XMLSerializable {
	public enum ItemType { GROUP, ENTITY }
	
	private final ItemType type;
	private Entity entity;
	private StackGroup group;
	
	/**
	 * Create a stack item representing a group.
	 */
	public StackItem(StackGroup group) {
		type = ItemType.GROUP;
		this.group = group;
	}
	
	/**
	 * Create a stack item representing an entity.
	 */
	public StackItem(Entity entity) {
		type = ItemType.ENTITY;
		this.entity = entity;
	}
	
	/**
	 * Get the entity this item represents.
	 * @return The entity, or <code>null</code> if this item represents a group.
	 */
	public Entity getEntity() {
		return entity;
	}
	
	/**
	 * Get the group this item represents.
	 * @return The group, or <code>null</code> if this item represents an entity.
	 */
	public StackGroup getGroup() {
		return group;
	}
	
	/**
	 * Get the object this item represents, regardless of what type it is.
	 */
	public Object get() {
		if (type == ItemType.ENTITY) {
			return entity;
		} else {
			return group;
		}
	}
	
	/**
	 * Get the type of object this item represents.
	 * @return Item type.
	 */
	public ItemType getType() {
		return type;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, get());
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof StackItem && hashCode() == obj.hashCode());
	}

	@Override
	public Element serialize(Document document) {
		if (type == ItemType.GROUP) {
			return getGroup().serialize(document);
		} else {
			return new EntitySerializer(getEntity()).serialize(document);
		}
	}
	
	/**
	 * Get the name if the enclosed item.
	 */
	public String getName() {
		if (type == ItemType.GROUP) {
			return group.getName();
		} else {
			return entity.getName();
		}
	}
	
	@Override
	public String toString() {
		return "StackItem: "+get().toString();
	}
}
