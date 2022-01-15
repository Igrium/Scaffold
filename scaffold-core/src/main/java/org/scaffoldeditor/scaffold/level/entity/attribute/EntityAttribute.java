package org.scaffoldeditor.scaffold.level.entity.attribute;

import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EntityAttribute extends Attribute<String> {
	
	public static String REGISTRY_NAME = "entity_attribute";
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<EntityAttribute>() {

			@Override
			public EntityAttribute create() {
				return new EntityAttribute("");
			}

			@Override
			public EntityAttribute deserialize(Element element) {
				return new EntityAttribute(element.getAttribute("target"));
			}
		});
	}
	
	private String target;
	
	public EntityAttribute(String target) {
		this.target = target;
		this.registryName = REGISTRY_NAME;
	}
	
	public EntityAttribute(Entity target) {
		this(target.getName());
	}

	public EntityAttribute() {
		this("");
	}

	@Override
	public String getValue() {
		return target;
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(registryName);
		element.setAttribute("target", target);
		return element;
	}

	@Override
	public EntityAttribute clone() {
		return this;
	}
	
	/**
	 * Evaluate this entity name according to an {@link Entity#entityNameOverride}
	 * @param owner Entity scope to evaluate in.
	 * @return Evaluated name.
	 */
	public String evaluate(Entity owner) {
		return owner.evaluateName(getValue());
	}

	@Override
	public String toString() {
		return "ref: "+getValue();
	}
}
