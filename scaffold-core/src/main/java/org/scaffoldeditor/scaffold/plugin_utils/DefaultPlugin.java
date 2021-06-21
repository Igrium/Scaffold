package org.scaffoldeditor.scaffold.plugin_utils;

import org.apache.logging.log4j.LogManager;
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
import org.scaffoldeditor.scaffold.level.entity.attribute.ContainerAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.DoubleAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EnumAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.FloatAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.IntAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.ListAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.LongAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.level.entity.game.ContainerEntity;
import org.scaffoldeditor.scaffold.level.entity.game.GameEntity;
import org.scaffoldeditor.scaffold.level.entity.game.RedstoneListener;
import org.scaffoldeditor.scaffold.level.entity.logic.Auto;
import org.scaffoldeditor.scaffold.level.entity.logic.CommandEntity;
import org.scaffoldeditor.scaffold.level.entity.logic.FunctionEntity;
import org.scaffoldeditor.scaffold.level.entity.logic.Relay;
import org.scaffoldeditor.scaffold.level.entity.util.ModelTest;
import org.scaffoldeditor.scaffold.level.entity.util.TestBrushEntity;
import org.scaffoldeditor.scaffold.level.entity.world.WorldStatic;
import org.scaffoldeditor.scaffold.level.entity.world.WorldTogglable;
import org.scaffoldeditor.scaffold.level.entity.world.SingleBlock;
import org.scaffoldeditor.scaffold.level.entity.world.WorldBrush;
import org.scaffoldeditor.scaffold.level.entity.world.WorldChunked;
import org.scaffoldeditor.scaffold.level.entity.world.WorldDynamic;

public class DefaultPlugin implements PluginInitializer {

	@Override
	public void initialize() {
		LogManager.getLogger().info("Initializing default plugin...");
		BlockCollectionManager.registerDefaults();

		WorldStatic.Register();
		WorldChunked.register();
		WorldBrush.register();
		SingleBlock.Register();
		Auto.Register();
		FunctionEntity.Register();
		Relay.Register();
		GameEntity.register();
		ModelTest.register();
		CommandEntity.register();
		RedstoneListener.register();
		WorldTogglable.register();
		WorldDynamic.register();
		ContainerAttribute.register();
		TestBrushEntity.register();
		
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
		ContainerEntity.register();
		
		StructureAsset.register();
		ConstructionAsset.register();
		ConstructionWorldAsset.register();
		BlockTextureAsset.register();
		
		SingleBlockTexture.register();
		NoiseBlockTexture.register();
		
//		WriteWorldStep.setWorldWriter(new QuerzWorldWriter());
	}

}
