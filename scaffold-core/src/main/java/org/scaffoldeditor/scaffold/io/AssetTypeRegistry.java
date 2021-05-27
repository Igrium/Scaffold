package org.scaffoldeditor.scaffold.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AssetTypeRegistry {
	
	/**
	 * The registry of asset types.
	 * <br>
	 * {file extension, asset type entry}
	 */
	public static final Map<String, AssetType<?>> registry = new HashMap<>();
	
	/**
	 * Load an asset from an input stream.
	 * @param in Input stream to load.
	 * @param extension Extension of the file (without the dot)
	 * @return Loaded asset.
	 */
	public static Object loadAsset(InputStream in, String extension) throws IOException {
		AssetType<?> type = registry.get(extension);
		if (type == null) {
			throw new IOException("Unknown file extension: "+extension);
		}
		return type.loadAsset(in);
	}
	
	/**
	 * Check if an asset type loads a subclass of the given class.
	 * @param extension File extension of asset type (without the dot).
	 * @param cls Class to check against.
	 */
	public static boolean isTypeAssignableTo(String extension, Class<?> cls) {
		AssetType<?> type = registry.get(extension);
		return (type != null && cls.isAssignableFrom(type.assetClass));
	}
	
	/**
	 * Get all the asset types that load a subclass of the given class.
	 * @param cls Class to check against.
	 * @return A set of all the valid file extensions (without the dot).
	 */
	public static Set<String> getTypesAssignableTo(Class<?> cls) {
		Set<String> set = new HashSet<>();
		
		for (String type : registry.keySet()) {
			if (cls.isAssignableFrom(registry.get(type).assetClass)) {
				set.add(type);
			}
		}
		
		return set;
	}
}
