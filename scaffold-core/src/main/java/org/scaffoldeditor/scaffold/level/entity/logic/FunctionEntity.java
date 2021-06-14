package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Map;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;

/**
 * Runs a function on the input "Run".
 * @author Igrium
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
	}
	
	public String getFunction() {
		return ((StringAttribute) getAttribute("function")).getValue();
	}

	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		return Map.of("function", new StringAttribute(""));
	}
}
