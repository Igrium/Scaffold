package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Map;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.IntAttribute;

/**
 * This class relays io from it's inputs to it's outputs
 * 
 * @author Igrium
 */
public class Relay extends Entity {
	
	public static void Register() {
		EntityRegistry.registry.put("logic_relay", new EntityFactory<Entity>() {		
			@Override
			public Entity create(Level level, String name) {
				return new Relay(level, name);
			}
		});
	}
	
	

	public Relay(Level level, String name) {
		super(level, name);
	}
	
	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		return Map.of("delay", new IntAttribute(0));
	}


//	@Override
//	public boolean compileLogic(Datapack datapack) {
//		super.compileLogic(datapack);
//
//		// Compile relay function
//		MCFunction function = new MCFunction(getFunctionName());
//
//		String[] outputCommands = compileOutput("OnTrigger", this);
//
//		for (String s : outputCommands) {
//			function.addCommand(s);
//		}
//		
//		datapack.functions.add(function);
//
//		return true;
//	}
	
	/**
	 * Get the name of the function this relay will generate.
	 * @return Function name.
	 */
	public String getFunctionName() {
		return "relay_" + getName();
	}
}
