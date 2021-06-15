package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
import org.scaffoldeditor.scaffold.level.render.BillboardRenderEntity;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.datapack.Function;

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
	public Collection<OutputDeclaration> getDeclaredOutputs() {
		Collection<OutputDeclaration> out = super.getDeclaredOutputs();
		out.add(new OutputDeclaration() {
			
			@Override
			public String getName() {
				return "on_datapack_load";
			}
			
			@Override
			public List<String> getArguements() {
				return Collections.emptyList();
			}
		});
		out.add(new OutputDeclaration() {
			
			@Override
			public String getName() {
				return "on_tick";
			}
			
			@Override
			public List<String> getArguements() {
				return Collections.emptyList();
			}
		});
		return out;
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		Function initFunction = new Function(getLevel().getName().toLowerCase(), getName()+"/init");
		initFunction.commands.addAll(compileOutput("on_datapack_loaded"));
		datapack.functions.add(initFunction);
		datapack.loadFunctions.add(initFunction.getMeta());
		
		Function tickFunction = new Function(getLevel().getName().toLowerCase(), getName()+"/tick");
		tickFunction.commands.addAll(compileOutput("on_tick"));
		datapack.functions.add(tickFunction);
		datapack.tickFunctions.add(tickFunction.getMeta());
		
		return super.compileLogic(datapack);
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
