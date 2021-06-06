package org.scaffoldeditor.scaffold.level.entity.attribute;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.scaffold.level.entity.attribute.EnumAttribute.DefaultEnums.PlaceholderEnum;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EnumAttribute<T extends Enum<T>> extends Attribute<T> {
	
	public static class DefaultEnums {
		public enum PlaceholderEnum {
			VALUE1,
			VALUE2
		}
	}
	
	public static final String REGISTRY_NAME = "enum_attribute";
	public static final Map<String, Class<? extends Enum<?>>> enumRegistry = new HashMap<>();

	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<EnumAttribute<?>>() {

			@Override
			public EnumAttribute<?> create() {
				return new EnumAttribute<PlaceholderEnum>(PlaceholderEnum.VALUE1);
			}

			// We're not actually doing anything with the value before returning it under
			// <?>, so typing wouldn't do anything anyway.
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public EnumAttribute<?> deserialize(Element element) {
				String className = element.getAttribute("enum_class");
				if (!enumRegistry.containsKey(className)) {
					throw new IllegalArgumentException("Unknown enum class: " + className);
				}
				Class<? extends Enum<?>> enumClass = enumRegistry.get(className);
				Enum<?> enm;
				try {
					enm = (Enum<?>) enumClass.getMethod("valueOf", String.class).invoke(null,
							element.getAttribute("value"));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					throw new AssertionError("Unable to load enum.", e);
				}
				return new EnumAttribute(enm);
			}
		});

		enumRegistry.put("placeholder", PlaceholderEnum.class);
	}

	private T value;

	public EnumAttribute(T value) {
		this.value = value;
		this.registryName = REGISTRY_NAME;
	}
	
	@Override
	public T getValue() {
		return value;
	}
	
	public Class<T> getEnumClass() {
		return value.getDeclaringClass();
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(REGISTRY_NAME);
		String enumClass = null;
		for (String className : enumRegistry.keySet()) {
			if (enumRegistry.get(className).equals(getEnumClass())) {
				enumClass = className;
				break;
			}
		}
		if (enumClass == null) {
			throw new IllegalStateException("Enum type " + getEnumClass().getName()
					+ " does not have a string registry name. Register it with EnumAttribute.enumRegistry.put(name, class).");
		}
		
		element.setAttribute("enum_class", enumClass);
		element.setAttribute("value", value.name());
		
		return element;
	}

	@Override
	public Attribute<T> clone() {
		return this;
	}

}
