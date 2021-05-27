package org.scaffoldeditor.scaffold.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Handles the parsing of a type of asset that AssetManager can load.
 * @author Igrium
 *
 * @param <T> Type of object that will be loaded.
 */
public abstract class AssetType<T> {
	
	/**
	 * The class of the object this asset load will return.
	 */
	public final Class<T> assetClass;
	
	public AssetType(Class<T> assetClass) {
		this.assetClass = assetClass;
	}
	
	/**
	 * Load an instance of the asset.
	 * @param in Input stream to load from.
	 * @return The loaded asset. Due to the way the asset cache works,
	 * this should be immutable.
	 */
	public abstract T loadAsset(InputStream in) throws IOException;
}
