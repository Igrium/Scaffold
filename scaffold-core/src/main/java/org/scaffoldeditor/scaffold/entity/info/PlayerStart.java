package org.scaffoldeditor.scaffold.entity.info;

import org.joml.Vector3d;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.logic.LogicEntity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

public class PlayerStart extends LogicEntity {
	
	public static void register() {
		EntityRegistry.registry.put("info_player_start", PlayerStart::new);
	}

	public PlayerStart(Level level, String name) {
		super(level, name);
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/start.png";
	}
	
	@Override
	protected Vector3d getRenderOffset() {
		return new Vector3d(.5f, 0, .5f);
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/info_player_start.sdoc", super.getDocumentation());
	}
}
