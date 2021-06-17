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
import org.scaffoldeditor.scaffold.level.entity.attribute.IntAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.LogicUtils;
import org.scaffoldeditor.scaffold.logic.datapack.Function;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.commands.FunctionCommand;

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
		Map<String, Attribute<?>> attributes = new HashMap<>();
		attributes.put("delay", new IntAttribute(0));
		attributes.put("executorOverride", new StringAttribute(""));
		
		return attributes;
	}
	
	@Override
	public Collection<OutputDeclaration> getDeclaredOutputs() {
		Collection<OutputDeclaration> out =  super.getDeclaredOutputs();
		out.add(new OutputDeclaration() {	
			@Override
			public String getName() {
				return "on_trigger";
			}
			
			@Override
			public List<String> getArguements() {
				return Collections.emptyList();
			}
		});
		return out;
	}

	@Override
	public Collection<InputDeclaration> getDeclaredInputs() {
		return List.of(new InputDeclaration() {

			@Override
			public String getName() {
				return "trigger";
			}

			@Override
			public List<String> getArguements() {
				return Collections.emptyList();
			}
		});
	}

	@Override
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source) {
		if (inputName.matches("trigger")) {
			FunctionCommand function = new FunctionCommand(getTriggerFunction());
			TargetSelector executor = getExecutorOverride();
			Command command;
			if (executor != null) {
				command = new ExecuteCommandBuilder().as(executor).run(function);
			} else {
				command = function;
			}
			
			return List.of(command);
		}

		return super.compileInput(inputName, args, source);
	}

	@Override
	public boolean compileLogic(Datapack datapack) {
		super.compileLogic(datapack);
		
		Function relayFunction = new Function(getTriggerFunction());
		for (Command command : compileOutput("on_trigger")) {
			relayFunction.commands.add(command);
		}
		
		datapack.functions.add(relayFunction);
		return true;
	}

	public TargetSelector getExecutorOverride() {
		String string = (String) getAttribute("executorOverride").getValue();
		return string.length() > 0 ? TargetSelector.fromString(string) : null;
	}
	
	public Identifier getTriggerFunction() {
		return LogicUtils.getEntityFunction(this, "trigger");
	}
}
