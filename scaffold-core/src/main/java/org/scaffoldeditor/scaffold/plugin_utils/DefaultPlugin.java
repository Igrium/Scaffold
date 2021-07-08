package org.scaffoldeditor.scaffold.plugin_utils;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.nbt.block.BlockCollectionManager;
import org.scaffoldeditor.scaffold.block_textures.NoiseBlockTexture;
import org.scaffoldeditor.scaffold.block_textures.SingleBlockTexture;
import org.scaffoldeditor.scaffold.io.BlockTextureAsset;
import org.scaffoldeditor.scaffold.io.ConstructionAsset;
import org.scaffoldeditor.scaffold.io.ConstructionWorldAsset;
import org.scaffoldeditor.scaffold.io.StructureAsset;
import org.scaffoldeditor.scaffold.io.TemplateFunctionAsset;
import org.scaffoldeditor.scaffold.level.entity.attribute.AssetAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockTextureAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.ContainerAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.DoubleAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EntityAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EnumAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.FilterAttribute;
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
import org.scaffoldeditor.scaffold.level.entity.game.RedstoneTrigger;
import org.scaffoldeditor.scaffold.level.entity.info.InfoTarget;
import org.scaffoldeditor.scaffold.level.entity.info.PlayerStart;
import org.scaffoldeditor.scaffold.level.entity.logic.Auto;
import org.scaffoldeditor.scaffold.level.entity.logic.CommandEntity;
import org.scaffoldeditor.scaffold.level.entity.logic.FunctionEntity;
import org.scaffoldeditor.scaffold.level.entity.logic.LogicFilter;
import org.scaffoldeditor.scaffold.level.entity.logic.LogicTeleport;
import org.scaffoldeditor.scaffold.level.entity.logic.LogicTimer;
import org.scaffoldeditor.scaffold.level.entity.logic.Relay;
import org.scaffoldeditor.scaffold.level.entity.logic.TriggerIndividual;
import org.scaffoldeditor.scaffold.level.entity.logic.TriggerMultiple;
import org.scaffoldeditor.scaffold.level.entity.path.PathNode;
import org.scaffoldeditor.scaffold.level.entity.path.PathTrain;
import org.scaffoldeditor.scaffold.level.entity.phys.PhysParent;
import org.scaffoldeditor.scaffold.level.entity.test.BlockModelTest;
import org.scaffoldeditor.scaffold.level.entity.test.ModelTest;
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
		SingleBlock.register();
		Auto.register();
		FunctionEntity.register();
		Relay.register();
		GameEntity.register();
		ModelTest.register();
		CommandEntity.register();
		RedstoneListener.register();
		RedstoneTrigger.register();
		WorldTogglable.register();
		WorldDynamic.register();
		ContainerAttribute.register();
		TriggerMultiple.register();
		TriggerIndividual.register();
		BlockModelTest.register();
		PlayerStart.register();
		InfoTarget.register();
		LogicTeleport.register();
		LogicTimer.register();
		LogicFilter.register();
		PathNode.register();
		PathTrain.register();
		PhysParent.register();
		
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
		EntityAttribute.register();
		FilterAttribute.register();
		
		StructureAsset.register();
		ConstructionAsset.register();
		ConstructionWorldAsset.register();
		BlockTextureAsset.register();
		TemplateFunctionAsset.register();
		
		SingleBlockTexture.register();
		NoiseBlockTexture.register();
		
//		WriteWorldStep.setWorldWriter(new QuerzWorldWriter());
	}

}
