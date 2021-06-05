package org.scaffoldeditor.scaffold.io;

import java.io.IOException;
import java.io.InputStream;

import org.scaffoldeditor.scaffold.block_textures.SerializableBlockTexture;
import org.scaffoldeditor.scaffold.serialization.BlockTextureReader;

public class BlockTextureAsset extends AssetLoader<SerializableBlockTexture> {
	
	public static void register() {
		AssetLoaderRegistry.registry.put("texture", new BlockTextureAsset());
	}

	public BlockTextureAsset() {
		super(SerializableBlockTexture.class);
	}

	@Override
	public SerializableBlockTexture loadAsset(InputStream in) throws IOException {
		return new BlockTextureReader(in).read();
	}
	
}
