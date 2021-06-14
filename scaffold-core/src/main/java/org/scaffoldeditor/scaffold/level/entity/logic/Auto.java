package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Collections;
import java.util.Map;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.render.BillboardRenderEntity;

/**
 * Fires an output on level load.
 * 
 * @author Igrium
 */
public class Auto extends Entity {
	
	public static void Register() {
		EntityRegistry.registry.put("logic_auto", new EntityFactory<Entity>() {		
			@Override
			public Entity create(Level level, String name) {
				return new Auto(level, name);
			}
		});
	}

	public Auto(Level level, String name) {
		super(level, name);
	}
	

	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		return Collections.emptyMap();
	}
	
	@Override
	public void onAdded() {
		super.onAdded();
		updateRenderEntities();
	}
	
	@Override
	public void onUpdateAttributes(boolean noRecompile) {
		super.onUpdateAttributes(noRecompile);
		updateRenderEntities();
	}

	@Override
	public void updateRenderEntities() {
		super.updateRenderEntities();
		updateRenderEntities(Collections.singleton(
				new BillboardRenderEntity(this, getPosition(), "billboard", "scaffold:textures/editor/auto.png")));
	}

}
