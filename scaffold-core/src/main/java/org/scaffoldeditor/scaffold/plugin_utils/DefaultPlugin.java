package org.scaffoldeditor.scaffold.plugin_utils;

import org.scaffoldeditor.nbt.block.BlockCollectionManager;
import org.scaffoldeditor.scaffold.level.entity.logic.Auto;
import org.scaffoldeditor.scaffold.level.entity.logic.FunctionEntity;
import org.scaffoldeditor.scaffold.level.entity.logic.Relay;
import org.scaffoldeditor.scaffold.level.entity.world.WorldStatic;
import org.scaffoldeditor.scaffold.level.entity.world.SingleBlock;

@ScaffoldPlugin
public class DefaultPlugin implements PluginInitializer {

	@Override
	public void initialize() {
		System.out.println("Initializing default plugin...");
		BlockCollectionManager.registerDefaults();

		WorldStatic.Register();
		SingleBlock.Register();
		Auto.Register();
		FunctionEntity.Register();
		Relay.Register();
	}

}
