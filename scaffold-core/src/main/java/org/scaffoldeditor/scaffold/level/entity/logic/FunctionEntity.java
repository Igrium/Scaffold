package org.scaffoldeditor.scaffold.level.entity.logic;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.io.Input;

/**
 * Runs a function on the input "Run".
 * @author Sam54123
 */
public class FunctionEntity extends Entity {
	
	public static void Register() {
		EntityRegistry.registry.put("logic_function", new EntityFactory<Entity>() {		
			@Override
			public Entity create(Level level, String name) {
				return new FunctionEntity(level, name);
			}
		});
	}
	
	public FunctionEntity(Level level, String name) {
		super(level, name);
		setAttribute("function", new StringAttribute(name), true);
		
		registerInput(new Input(this) {

			@Override
			public String getName() {
				return "Run";
			}

			@Override
			public boolean takesArgs() {
				return false;
			}

			@Override
			public String getCommand(Entity instigator, Entity caller, String[] args) {
				return "function "+getFunction();
			}
			
		});
	}
	
	public String getFunction() {
		return ((StringAttribute) getAttribute("function")).getValue();
	}
	
	@Override
	public String getRenderAsset() {
		return "scaffold/textures/editor/function.png";
	}

}
