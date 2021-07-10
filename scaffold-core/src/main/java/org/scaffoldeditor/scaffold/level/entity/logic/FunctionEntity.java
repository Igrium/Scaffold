package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.FunctionCommand;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

/**
 * Runs a function on the input "Run".
 * @author Igrium
 */
public class FunctionEntity extends LogicEntity {
	
	public static void register() {
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
	
	public Identifier getFunction() {
		String namespace = (String) getAttribute("namespace").getValue();
		String function = (String) getAttribute("function").getValue();
		return new Identifier(namespace, function);
	}
	
	@Override
	public Collection<InputDeclaration> getDeclaredInputs() {
		Collection<InputDeclaration> inputs = super.getDeclaredInputs();
		inputs.add(new InputDeclaration() {

			@Override
			public String getName() {
				return "execute";
			}

			@Override
			public List<String> getArguements() {
				return Collections.emptyList();
			}
		});
		return inputs;
	}
	
	@Override
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source, Entity instigator) {
		if (inputName.equals("execute")) {
			return List.of(new FunctionCommand(getFunction()));
		}
		return super.compileInput(inputName, args, source, instigator);
	}

	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = new HashMap<>();
		map.put("namespace", new StringAttribute(getProject().getName().toLowerCase()));
		map.put("function", new StringAttribute(""));
		return map;
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/script.png";
	}
	
	@Override
	protected float getRenderScale() {
		return .5f;
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/logic_function.sdoc");
	}
}
