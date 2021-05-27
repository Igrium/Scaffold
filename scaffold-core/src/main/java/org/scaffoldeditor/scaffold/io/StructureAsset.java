package org.scaffoldeditor.scaffold.io;

import java.io.IOException;
import java.io.InputStream;

import org.scaffoldeditor.nbt.schematic.Structure;

import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.tag.CompoundTag;

public class StructureAsset extends BlockCollectionAsset<Structure> {
	
	public StructureAsset() {
		super(Structure.class);
	}
	
	public static void register() {
		AssetTypeRegistry.registry.put("nbt", new StructureAsset());
	}
 
	@Override
	public Structure loadAsset(InputStream in) throws IOException {
		CompoundTag map = (CompoundTag) new NBTDeserializer(true).fromStream(in).getTag();

		if (map == null) {
			throw new IOException("Improperly formatted structure file!");
		}
		
		return Structure.fromCompoundMap(map);
	}

}
