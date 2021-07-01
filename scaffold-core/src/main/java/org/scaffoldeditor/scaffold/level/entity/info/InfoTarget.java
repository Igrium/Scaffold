package org.scaffoldeditor.scaffold.level.entity.info;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.logic.LogicEntity;

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
	public Map<String, Attribute<?>> getDefaultAttributes() {
		return new HashMap<>();
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/target.png";
	}

}
