package org.scaffoldeditor.scaffold.io;

import java.io.IOException;
import java.io.InputStream;

import org.scaffoldeditor.nbt.io.ConstructionFormat;
import org.scaffoldeditor.nbt.schematic.Construction.ConstructionSegment;

public class ConstructionAsset extends BlockCollectionAsset<ConstructionSegment> {
	
	public static void register() {
		AssetTypeRegistry.registry.put("construction", new ConstructionAsset());
	}
	
	public ConstructionAsset() {
		super(ConstructionSegment.class);
	}
	
	private ConstructionFormat format = new ConstructionFormat();
 
	@Override
	public ConstructionSegment loadAsset(InputStream in) throws IOException {
		return format.readBlockCollection(in);
	}

}
