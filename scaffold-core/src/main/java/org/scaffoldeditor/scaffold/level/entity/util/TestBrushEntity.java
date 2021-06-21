package org.scaffoldeditor.scaffold.level.entity.util;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;

public class TestBrushEntity extends ToolBrushEntity {
	
	public static void register() {
		EntityRegistry.registry.put("test_brush", TestBrushEntity::new);
	}

	public TestBrushEntity(Level level, String name) {
		super(level, name);
	}

	@Override
	public String getTexture() {
		return "scaffold:textures/editor/auto.png";
	}

}
