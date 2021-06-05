package org.scaffoldeditor.scaffold.io;

import org.scaffoldeditor.nbt.block.SizedBlockCollection;

public abstract class BlockCollectionAsset<T extends SizedBlockCollection> extends AssetLoader<T> {

	public BlockCollectionAsset(Class<T> assetClass) {
		super(assetClass);
	}

}
