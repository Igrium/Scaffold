package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.List;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.io.Input;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.MCFunction;

/**
 * This class relays io from it's inputs to it's outputs
 * 
 * @author Sam54123
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
		setAttribute("delay", 0);
		
		// Called to activate the relay.
		registerInput(new Input(this) {

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "Trigger";
			}

			@Override
			public boolean takesArgs() {
				return false;
			}

			@Override
			public String getCommand(Entity instigator, Entity caller, String[] args) {
				if ((int) getAttribute("delay") <= 0) { // if delay < 0, ignore it.
					return "function "+getLevel().getDatapack().formatFunctionCall(getFunctionName());
				}  else {
					return "schedule function "+getLevel().getDatapack().formatFunctionCall(getFunctionName())+" "+getAttribute("delay");
				}
			}

		});
	}

	@Override
	public List<AttributeDeclaration> getAttributeFields() {
		List<AttributeDeclaration> attributes = super.getAttributeFields();
		attributes.add(new AttributeDeclaration("delay", Integer.class));
		return attributes;
	}

	@Override
	public boolean compileLogic(Datapack datapack) {
		super.compileLogic(datapack);

		// Compile relay function
		MCFunction function = new MCFunction(getFunctionName());

		String[] outputCommands = compileOutput("OnTrigger", this);

		for (String s : outputCommands) {
			function.addCommand(s);
		}
		
		datapack.functions.add(function);

		return true;
	}
	
	/**
	 * Get the name of the function this relay will generate.
	 * @return Function name.
	 */
	public String getFunctionName() {
		return "relay_" + getName();
	}
	
	@Override
	public String getRenderAsset() {
		return "scaffold/textures/editor/relay.png";
	}

}
