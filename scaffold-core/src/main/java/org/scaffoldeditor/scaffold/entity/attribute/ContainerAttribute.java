package org.scaffoldeditor.scaffold.entity.attribute;

import java.io.IOException;

import org.scaffoldeditor.scaffold.entity.Entity;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.Tag;

public class ContainerAttribute extends Attribute<ListTag<CompoundTag>> {
	
	public static final String REGISTRY_NAME = "container";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<ContainerAttribute>() {

			@Override
			public ContainerAttribute create() {
				return new ContainerAttribute();
			}

			@Override
			public ContainerAttribute deserialize(Element element) {
				try {
					Tag<?> nbt = SNBTUtil.fromSNBT(element.getTextContent());
					if (!(nbt instanceof ListTag)) {
						throw new IOException("Container NBT is not a list tag!");
					}
					
					return new ContainerAttribute(((ListTag<?>) nbt).asCompoundTagList());
				} catch (DOMException | IOException e) {
					throw new AssertionError("Error parsing container NBT!", e);
				}
			}
		});
	}
	
	public final ListTag<CompoundTag> items;
	
	public ContainerAttribute(ListTag<CompoundTag> value) {
		this.items = value;
		this.registryName = REGISTRY_NAME;
	}
	
	public ContainerAttribute() {
		this(new ListTag<>(CompoundTag.class));
	}
	
	/**
	 * Get the list of items that this attribute represents. Each item is a Compound
	 * tag, including the <code>slot</code> tag. <br>
	 * {@link Entity#onUpdateAttributes} should be called after updating if an
	 * recompilation is desired.
	 */
	public ListTag<CompoundTag> getValue() {
		return items;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(REGISTRY_NAME);
		try {
			element.setTextContent(SNBTUtil.toSNBT(items));
		} catch (DOMException | IOException e) {
			throw new AssertionError(e);
		}
		
		return element;
	}

	@Override
	public ContainerAttribute clone() {
		return new ContainerAttribute(items.clone());
	}

}
