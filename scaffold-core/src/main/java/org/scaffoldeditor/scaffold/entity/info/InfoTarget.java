package org.scaffoldeditor.scaffold.entity.info;

import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.logic.LogicEntity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

/**
 * An extreamly simple entity that serves no purpose except to provide its
 * coordinates to other entities at compile time.
 * 
 * @author Igrium
 */
public class InfoTarget extends LogicEntity {
	
	public static void register() {
		EntityRegistry.registry.put("info_target", InfoTarget::new);
	}

	public InfoTarget(Level level, String name) {
		super(level, name);
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/target.png";
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/info_target.sdoc", super.getDocumentation());
	}
}
