package org.scaffoldeditor.scaffold.plugin_utils;

import org.scaffoldeditor.nbt.block.BlockCollectionManager;
import org.scaffoldeditor.scaffold.level.entity.logic.Auto;
import org.scaffoldeditor.scaffold.level.entity.logic.FunctionEntity;
import org.scaffoldeditor.scaffold.level.entity.logic.Relay;
import org.scaffoldeditor.scaffold.level.entity.world.WorldStatic;
import org.scaffoldeditor.scaffold.level.entity.world.SingleBlock;

public class DefaultPlugin implements PluginInitializer {

	@Override
	public void initialize() {
		BlockCollectionManager.registerDefaults();

		WorldStatic.Register();
		SingleBlock.Register();
		Auto.Register();
		FunctionEntity.Register();
		Relay.Register();
	}

}
