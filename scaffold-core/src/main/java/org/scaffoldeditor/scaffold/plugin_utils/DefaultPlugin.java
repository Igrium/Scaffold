package org.scaffoldeditor.scaffold.plugin_utils;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.nbt.block.BlockCollectionManager;
import org.scaffoldeditor.scaffold.block_textures.NoiseBlockTexture;
import org.scaffoldeditor.scaffold.block_textures.SingleBlockTexture;
import org.scaffoldeditor.scaffold.entity.attribute.AssetAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.BlockAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.BlockTextureAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.ContainerAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.DoubleAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.EntityAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.EnumAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.FilterAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.FloatAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.IntAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.ListAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.LongAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.entity.game.ContainerEntity;
import org.scaffoldeditor.scaffold.entity.game.GameEntity;
import org.scaffoldeditor.scaffold.entity.game.RedstoneListener;
import org.scaffoldeditor.scaffold.entity.game.RedstoneTrigger;
import org.scaffoldeditor.scaffold.entity.info.InfoTarget;
import org.scaffoldeditor.scaffold.entity.info.PlayerStart;
import org.scaffoldeditor.scaffold.entity.logic.Auto;
import org.scaffoldeditor.scaffold.entity.logic.CommandEntity;
import org.scaffoldeditor.scaffold.entity.logic.FunctionEntity;
import org.scaffoldeditor.scaffold.entity.logic.LogicEntryPoint;
import org.scaffoldeditor.scaffold.entity.logic.LogicFilter;
import org.scaffoldeditor.scaffold.entity.logic.LogicTeleport;
import org.scaffoldeditor.scaffold.entity.logic.LogicTimer;
import org.scaffoldeditor.scaffold.entity.logic.Relay;
import org.scaffoldeditor.scaffold.entity.logic.TriggerIndividual;
import org.scaffoldeditor.scaffold.entity.logic.TriggerMultiple;
import org.scaffoldeditor.scaffold.entity.path.PathNode;
import org.scaffoldeditor.scaffold.entity.path.PathTrain;
import org.scaffoldeditor.scaffold.entity.phys.PhysParent;
import org.scaffoldeditor.scaffold.entity.test.BlockModelTest;
import org.scaffoldeditor.scaffold.entity.test.ModelTest;
import org.scaffoldeditor.scaffold.entity.world.SingleBlock;
import org.scaffoldeditor.scaffold.entity.world.WorldBrush;
import org.scaffoldeditor.scaffold.entity.world.WorldDynamic;
import org.scaffoldeditor.scaffold.entity.world.WorldStatic;
import org.scaffoldeditor.scaffold.entity.world.WorldTogglable;
import org.scaffoldeditor.scaffold.io.BlockTextureAsset;
import org.scaffoldeditor.scaffold.io.ConstructionAsset;
import org.scaffoldeditor.scaffold.io.ConstructionWorldAsset;
import org.scaffoldeditor.scaffold.io.DocAsset;
import org.scaffoldeditor.scaffold.io.StringAsset;
import org.scaffoldeditor.scaffold.io.StructureAsset;
import org.scaffoldeditor.scaffold.io.TemplateFunctionAsset;

public class DefaultPlugin implements PluginInitializer {

	@Override
	public void initialize() {
		LogManager.getLogger().info("Initializing default plugin...");
		BlockCollectionManager.registerDefaults();
		
		/* ENTITIES */
		WorldStatic.register();
		// WorldChunked.register();
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
		LogicEntryPoint.register();
		PathNode.register();
		PathTrain.register();
		PhysParent.register();
		
		/* ATTRIBUTES */
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
		
		/* ASSETS */
		StructureAsset.register();
		ConstructionAsset.register();
		ConstructionWorldAsset.register();
		BlockTextureAsset.register();
		TemplateFunctionAsset.register();
		StringAsset.register();
		DocAsset.register();
		
		/* BLOCK TEXTURES */
		SingleBlockTexture.register();
		NoiseBlockTexture.register();
		
//		WriteWorldStep.setWorldWriter(new QuerzWorldWriter());
	}

}
