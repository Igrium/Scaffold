package org.scaffoldeditor.scaffold.plugin_utils;

import org.scaffoldeditor.nbt.block.BlockCollectionManager;
import org.scaffoldeditor.scaffold.block_textures.NoiseBlockTexture;
import org.scaffoldeditor.scaffold.block_textures.SingleBlockTexture;
import org.scaffoldeditor.scaffold.io.BlockTextureAsset;
import org.scaffoldeditor.scaffold.io.ConstructionAsset;
import org.scaffoldeditor.scaffold.io.ConstructionWorldAsset;
import org.scaffoldeditor.scaffold.io.StructureAsset;
import org.scaffoldeditor.scaffold.level.entity.attribute.AssetAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockTextureAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.DoubleAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EnumAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.FloatAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.IntAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.ListAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.LongAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.level.entity.logic.Auto;
import org.scaffoldeditor.scaffold.level.entity.logic.FunctionEntity;
import org.scaffoldeditor.scaffold.level.entity.logic.Relay;
import org.scaffoldeditor.scaffold.level.entity.world.WorldStatic;
import org.scaffoldeditor.scaffold.level.entity.world.SingleBlock;
import org.scaffoldeditor.scaffold.level.entity.world.WorldBrush;
import org.scaffoldeditor.scaffold.level.entity.world.WorldChunked;

public class DefaultPlugin implements PluginInitializer {

	@Override
	public void initialize() {
		System.out.println("Initializing default plugin...");
		BlockCollectionManager.registerDefaults();

		WorldStatic.Register();
		WorldChunked.register();
		WorldBrush.register();
		SingleBlock.Register();
		Auto.Register();
		FunctionEntity.Register();
		Relay.Register();
		
		StringAttribute.register();
		NBTAttribute.register();
		IntAttribute.register();
		FloatAttribute.register();
		DoubleAttribute.register();
		LongAttribute.register();
		BooleanAttribute.register();
		VectorAttribute.register();
		ListAttribute.register();
		BlockAttribute.register();
		BlockTextureAttribute.register();
		AssetAttribute.register();
		EnumAttribute.register();
		
		StructureAsset.register();
		ConstructionAsset.register();
		ConstructionWorldAsset.register();
		BlockTextureAsset.register();
		
		SingleBlockTexture.register();
		NoiseBlockTexture.register();
		
//		WriteWorldStep.setWorldWriter(new QuerzWorldWriter());
	}

}
