package org.scaffoldeditor.scaffold.entity.test;

import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.util.ToolBrushEntity;
import org.scaffoldeditor.scaffold.level.Level;

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
