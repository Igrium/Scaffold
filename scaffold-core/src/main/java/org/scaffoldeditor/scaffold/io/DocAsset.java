package org.scaffoldeditor.scaffold.io;

import java.io.IOException;

import org.scaffoldeditor.scaffold.sdoc.SDoc;

public class DocAsset extends TextAssetLoader<SDoc> {
	
	public static void register() {
		AssetLoaderRegistry.registry.put("sdoc", new DocAsset());
	}

	public DocAsset() {
		super(SDoc.class);
	}

	@Override
	public SDoc parse(String contents) throws IOException {
		try {
			return SDoc.parse(contents);
		} catch (IllegalArgumentException e) {
			throw new IOException(e.getMessage(), e);
		}
	}
	
}
