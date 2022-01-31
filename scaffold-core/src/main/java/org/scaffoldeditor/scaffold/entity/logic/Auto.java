package org.scaffoldeditor.scaffold.entity.logic;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.EntityFactory;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.datapack.Function;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

/**
 * Fires an output on level load.
 * 
 * @author Igrium
 */
public class Auto extends LogicEntity {
	
	public static void register() {
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
		initFunction.commands.addAll(compileOutput("on_datapack_load"));
		datapack.functions.add(initFunction);
		datapack.loadFunctions.add(initFunction.getID());
		
		Function tickFunction = new Function(getLevel().getName().toLowerCase(), getName()+"/tick");
		tickFunction.commands.addAll(compileOutput("on_tick"));
		datapack.functions.add(tickFunction);
		datapack.tickFunctions.add(tickFunction.getID());
		
		return super.compileLogic(datapack);
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/auto.png";
	}

	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/logic_auto.sdoc", super.getDocumentation());
	}

}
