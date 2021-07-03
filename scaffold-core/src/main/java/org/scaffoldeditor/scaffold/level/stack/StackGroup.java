package org.scaffoldeditor.scaffold.level.stack;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.stack.StackItem.ItemType;
import org.scaffoldeditor.scaffold.serialization.EntitySerializer;
import org.scaffoldeditor.scaffold.serialization.LoadContext;
import org.scaffoldeditor.scaffold.serialization.XMLSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents a group on the level stack.
 * @author Igrium
 */
public class StackGroup extends AbstractCollection<Entity> implements XMLSerializable {
	
	/**
	 * Deserialize a stack group from XML. Doesn't actually add to the level stack.
	 * Automatically calles {@link Entity#onUnserialized(Element)}.
	 * 
	 * @param xml     Input XML.
	 * @param level   Level to give to entity constructors.
	 * @param context The load context.
	 * @return Deserialized stack group.
	 */
	public static StackGroup deserialize(Element xml, Level level, LoadContext context) {
		List<StackItem> items = new ArrayList<>();
		NodeList children = xml.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				Element element = (Element) child;
				
				if (element.getTagName().equals("group")) {
					items.add(new StackItem(StackGroup.deserialize(element, level, context)));
					
				} else {
					Entity ent = EntitySerializer.loadEntity(element, level);
					ent.onUnserialized(element);
					items.add(new StackItem(ent));
				}
			}
		}
		String name = xml.getAttribute("name");
		return new StackGroup(items, name != null ? name : "");
	}
	
	/**
	 * The items in the group, mutable. If part of the level stack,
	 * {@link Level#updateLevelStack()} should be called after modifying.
	 */
	public final List<StackItem> items;
	
	private String name;
	
	public StackGroup(List<StackItem> items, String name) {
		this.items = items;
		this.name = name;
	}
	
	public StackGroup(String name) {
		this.items = new ArrayList<>();
		this.name = name;
	}
	
	/**
	 * Append an entity to the stack.
	 * @param entity Entity to add.
	 */
	public boolean add(Entity entity) {
		return items.add(new StackItem(entity));
	}
	
	/**
	 * Remove the first instance of an entity from the stack.
	 * @param entity Entity to remove.
	 * @return Whether the entity was in the stack.
	 */
	public boolean remove(Object entity) {
		if (!(entity instanceof Entity)) {
			return false;
		}
		
		for (int i = 0; i < items.size(); i++) {
			StackItem item = items.get(i);
			if (entity.equals(item.getEntity())) {
				items.remove(i);
				return true;
			} else if (item.getType() == ItemType.GROUP) {
				if (item.getGroup().remove(entity)) return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the first sub-group in this group to contain this item as one of it's direct children.
	 * @param entity The entity to search for.
	 * @return The owning group, or <code>null</code> if the entity doesn't exist within this group.
	 */
	public StackGroup getOwningGroup(Entity entity) {
		for (StackItem item : items) {
			if (item.get().equals(entity)) return this;
			if (item.getType() == ItemType.GROUP) {
				StackGroup owner = item.getGroup().getOwningGroup(entity);
				if (owner != null) return owner;
			}
		}
		return null;
	}
	
	public StackGroup getOwningGroup(StackItem item) {
		for (StackItem it : items) {
			if (it.equals(item)) return this;
			if (it.getType() == ItemType.GROUP) {
				StackGroup owner = it.getGroup().getOwningGroup(item);
				if (owner != null) return owner;
			}
		}
		return null;
	}
	
	/**
	 * Check if the passed group is or is a child of this group.
	 * @param group Group to check.
	 */
	public boolean containsGroup(StackGroup group) {
		if (group == this) return true;
		for (StackItem item : this.items) {
			if (item.getType() == ItemType.GROUP && item.getGroup().containsGroup(group)) return true;
		}
		return false;
	}
	
	/**
	 * Collapse this stack group into a list of entities in compile order.
	 * @return Unmodifiable list of entities.
	 */
	public List<Entity> collapse() {
		return List.copyOf(this);
	}

	@Override
	public Iterator<Entity> iterator() {
		return new Iterator<Entity>() {
			
			Iterator<Entity> sub = null;
			int index = 0;

			@Override
			public boolean hasNext() {
				if (sub != null && sub.hasNext()) {
					return true;
				}
				// Even if there are more groups, they may not have items.
				for (int i = index; i < items.size(); i++) {
					StackItem item = items.get(i);
					if (item.getType() == ItemType.ENTITY || item.getGroup().size() > 0) {
						return true;
					}
				}
				return false;
			}

			@Override
			public Entity next() {
				if (sub != null) {
					if (sub.hasNext()) {
						return sub.next();
					} else {
						sub = null;
					}
				}
				
				StackItem item = items.get(index);
				index++;
				if (item.getType() == ItemType.GROUP) {
					StackGroup group = item.getGroup();
					if (group.items.size() > 0) {
						sub = item.getGroup().iterator();
						return sub.next();
					} else {
						return next();
					}
				} else {
					return item.getEntity();
				}
			}
		};
	}

	@Override
	public int size() {
		int size = 0;
		for (StackItem item : items) {
			if (item.getType() == ItemType.GROUP) {
				size += item.getGroup().size();
			} else {
				size++;
			}
		}
		
		return size;
	}
	
	/**
	 * Get this group's name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set this group's name.
	 * @param name New name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(items.toArray());
	}
	
	public int indexOf(Entity entity) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).get().equals(entity)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Make a semi-light clone of this stack group. Sub-groups in this group will be
	 * cloned recursively, but the actual entities that they're referencing will
	 * not.
	 */
	public StackGroup copy() {
		List<StackItem> list = new ArrayList<>();
		for (StackItem item : items) {
			if (item.getType() == ItemType.GROUP) {
				list.add(new StackItem(item.getGroup().copy()));
			} else {
				list.add(item);
			}
		}
		
		return new StackGroup(items, name);
	}
	
	@Override
	public String toString() {
		return items.toString();
	}

	@Override
	public Element serialize(Document document) {
		return serialize(document, null);
	}
	
	public Element serialize(Document document, String nameOverride) {
		Element element = document.createElement(nameOverride != null ? nameOverride : "group");
		for (StackItem item : items) {
			element.appendChild(item.serialize(document));
		}
		element.setAttribute("name", name);
		
		return element;
	}

}
