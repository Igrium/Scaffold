package org.scaffoldeditor.scaffold.entity.logic;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.EntityFactory;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.Level;
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

	@Attrib
	StringAttribute namespace = new StringAttribute(getProject().getName().toLowerCase());

	@Attrib
	StringAttribute function = new StringAttribute("");
	
	public Identifier getFunction() {
		return new Identifier(namespace.getValue(), function.getValue());
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
	public String getSprite() {
		return "scaffold:textures/editor/script.png";
	}
	
	@Override
	protected float getRenderScale() {
		return .5f;
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/logic_function.sdoc", super.getDocumentation());
	}
}
