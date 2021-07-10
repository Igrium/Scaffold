package org.scaffoldeditor.scaffold.io;

/**
 * Reads simple text formats such as {@code txt}.
 * @author Igrium
 */
public class StringAsset extends TextAssetLoader<String> {
	
	public static void register() {
		StringAsset instance = new StringAsset();
		AssetLoaderRegistry.registry.put("txt", instance);
		AssetLoaderRegistry.registry.put("md", instance);
	}

	public StringAsset() {
		super(String.class);
	}

	@Override
	public String parse(String contents) {
		return contents;
	}

}
