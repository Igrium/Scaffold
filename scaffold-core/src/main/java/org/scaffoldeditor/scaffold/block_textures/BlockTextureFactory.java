package org.scaffoldeditor.scaffold.block_textures;

public interface BlockTextureFactory<T extends SerializableBlockTexture> {
	T create();
}
