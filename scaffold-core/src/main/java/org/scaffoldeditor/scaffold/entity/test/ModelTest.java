package org.scaffoldeditor.scaffold.entity.test;

import java.util.Set;

import org.joml.Vector3d;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.EntityFactory;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.render.ModelRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

public class ModelTest extends Entity {
	
	public static String REGISTRY_NAME = "util_modeltest";
	
	public static void register() {
		EntityRegistry.registry.put(REGISTRY_NAME, new EntityFactory<ModelTest>() {
			
			@Override
			public ModelTest create(Level level, String name) {
				return new ModelTest(level, name);
			}
		});
	}

	public ModelTest(Level level, String name) {
		super(level, name);
	}

	@Attrib
	StringAttribute model = new StringAttribute("");

	@Override
	public Set<RenderEntity> getRenderEntities() {
		Set<RenderEntity> set = super.getRenderEntities();
		String model = this.model.getValue();
		set.add(new ModelRenderEntity(this, getPreviewPosition(), new Vector3d(0,0,0), "model", model));
		return set;
	}

}
