package org.scaffoldeditor.scaffold.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Utility superclass for asset loaders that load text-based files. Loads the entire file at once.
 * @author Igrium
 *
 * @param <T> Type of object that will be loaded.
 */
public abstract class TextAssetLoader<T> extends AssetLoader<T> {

	public TextAssetLoader(Class<T> assetClass) {
		super(assetClass);
	}
	
	@Override
	public T loadAsset(InputStream in) throws IOException {
		String raw = new BufferedReader(new InputStreamReader(in)).lines()
				.collect(Collectors.joining(System.lineSeparator()));
		return parse(raw);
	}
	
	/**
	 * Parse the text file contents. 
	 * @param contents File contents.
	 * @return The loaded asset. Due to the way the asset cache works, this should be immutable.
	 * @throws IOException If an IO Exception occurs.
	 */
	public abstract T parse(String contents) throws IOException;
}
