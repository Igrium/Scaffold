package org.scaffoldeditor.scaffold.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

public class AssetLoaderRegistry {
	
	/**
	 * The registry of asset types.
	 * <br>
	 * {file extension, asset type entry}
	 */
	public static final Map<String, AssetLoader<?>> registry = new HashMap<>();
	
	/**
	 * Load an asset from an input stream.
	 * @param in Input stream to load.
	 * @param extension Extension of the file (without the dot)
	 * @return Loaded asset.
	 */
	public static Object loadAsset(InputStream in, String extension) throws IOException {
		AssetLoader<?> type = registry.get(extension);
		if (type == null) {
			throw new IOException("Unknown file extension: "+extension);
		}
		return type.loadAsset(in);
	}
	
	/**
	 * Get the asset loader that would be used to load a particular file.
	 * 
	 * @param file Pathname of file.
	 * @return The asset loader, or <code>null</code> if it doesn't exist.
	 */
	public static AssetLoader<?> getAssetLoader(String file) {
		return registry.get(FilenameUtils.getExtension(file));
	}
	
	/**
	 * Check if an asset type loads a subclass of the given class.
	 * @param extension File extension of asset type (without the dot).
	 * @param cls Class to check against.
	 */
	public static boolean isTypeAssignableTo(String extension, Class<?> cls) {
		AssetLoader<?> type = registry.get(extension);
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
