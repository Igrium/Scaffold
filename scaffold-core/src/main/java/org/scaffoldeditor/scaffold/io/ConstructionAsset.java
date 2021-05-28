package org.scaffoldeditor.scaffold.io;

import java.io.IOException;
import java.io.InputStream;

import org.scaffoldeditor.nbt.io.ConstructionFormat;
import org.scaffoldeditor.nbt.schematic.Construction;

public class ConstructionAsset extends BlockCollectionAsset<Construction> {
	
	public static void register() {
		AssetTypeRegistry.registry.put("construction", new ConstructionAsset());
	}
	
	public ConstructionAsset() {
		super(Construction.class);
	}
	
	private ConstructionFormat format = new ConstructionFormat();
 
	@Override
	public Construction loadAsset(InputStream in) throws IOException {
		return format.readBlockCollection(in);
	}

}
