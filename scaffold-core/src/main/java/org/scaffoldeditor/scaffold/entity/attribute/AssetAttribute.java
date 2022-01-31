package org.scaffoldeditor.scaffold.entity.attribute;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;
import org.scaffoldeditor.nbt.block.ChunkedBlockCollection;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.scaffold.io.AssetLoaderRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AssetAttribute extends Attribute<String> {
	
	public static final String REGISTRY_NAME = "asset_attribute";
	/**
	 * A map of all the asset type names this attribute can hold and their
	 * corrisponding object class.
	 */
	public static final Map<String, AssetTypeEntry> assetTypes = new HashMap<>();
	
	public static class AssetTypeEntry {
		public final Class<?> cls;
		public final String name;
		public AssetTypeEntry(String name, Class<?> cls) {
			this.name = name;
			this.cls = cls;
		}
	}
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<AssetAttribute>() {

			@Override
			public AssetAttribute create() {
				return new AssetAttribute("any", "");
			}

			@Override
			public AssetAttribute deserialize(Element element) {
				String type = element.getAttribute("type");
				String value = element.getAttribute("value");
				if (assetTypes.containsKey(type)) {
					return new AssetAttribute(type, value);
				} else {
					return new AssetAttribute("any", value);
				}
			}
		});
		loadDefaultTypes();
	}
	
	public static void loadDefaultTypes() {
		assetTypes.put("any", new AssetTypeEntry("Assets", Object.class));
		assetTypes.put("schematic", new AssetTypeEntry("Schematic Files", SizedBlockCollection.class));
		assetTypes.put("chunked", new AssetTypeEntry("Chunked Schematic Files", ChunkedBlockCollection.class));
		assetTypes.put("json", new AssetTypeEntry("JSON Files", JSONObject.class));
	}
	
	/**
	 * Get the first asset type registry name of the asset type that can load a
	 * certian class.
	 * 
	 * @param cls Class to check for.
	 * @return Registry name, or null if it doesn't exist.
	 */
	public static String assetTypeOf(Class<?> cls) {
		for (String type : assetTypes.keySet()) {
			if (assetTypes.get(type).cls.equals(cls)) return type;
		}
		return null;
	}
	
	private String value = "";
	private String type = "any";
	
	public AssetAttribute(String assetType, String value) {
		if (assetTypes.containsKey(assetType)) {
			this.type = assetType;
		} else {
			LogManager.getLogger().error("Unknown asset attribute type: '" + assetType + "'. Defaulting to 'any'.");
			this.type = "any";
		}
		
		this.value = value;
		this.registryName = REGISTRY_NAME;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	public String getAssetTypeName() {
		return type;
	}
	
	public AssetTypeEntry getAssetType() {
		return assetTypes.get(type);
	}
	
	/**
	 * Get the class of object that this attribute should load.
	 */
	public Class<?> getAssetClass() {
		return assetTypes.get(type).cls;
	}
	
	/**
	 * Get all the file extensions that are applicable to this attribute.
	 */
	public Set<String> getAssignableExtensions() {
		return AssetLoaderRegistry.getTypesAssignableTo(getAssetClass());
	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(REGISTRY_NAME);
		element.setAttribute("value", value);
		element.setAttribute("type", type);
		return element;
	}

	@Override
	public Attribute<String> clone() {
		return this;
	}

}
