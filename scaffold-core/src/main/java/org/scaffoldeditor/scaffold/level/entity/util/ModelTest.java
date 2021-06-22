package org.scaffoldeditor.scaffold.level.entity.util;

import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
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
	
	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		return Map.of("model", new StringAttribute(""));
	}

	@Override
	public Set<RenderEntity> getRenderEntities() {
		Set<RenderEntity> set = super.getRenderEntities();
		String model = ((StringAttribute) getAttribute("model")).getValue();
		set.add(new ModelRenderEntity(this, getPreviewPosition(), new Vector3f(0,0,0), "model", model));
		return set;
	}

}
