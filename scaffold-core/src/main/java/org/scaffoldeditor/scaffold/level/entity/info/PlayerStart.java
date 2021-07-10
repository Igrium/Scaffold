package org.scaffoldeditor.scaffold.level.entity.info;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.logic.LogicEntity;
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
	public Map<String, Attribute<?>> getDefaultAttributes() {
		return new HashMap<>();
	}
	
	@Override
	protected Vector3f getRenderOffset() {
		return new Vector3f(.5f, 0, .5f);
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/info_player_start.sdoc");
	}
}
