package org.scaffoldeditor.scaffold.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
}
