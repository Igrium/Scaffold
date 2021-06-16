package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;

/**
 * Executes a single Minecraft command when compiled.
 * @author Igrium
 */
public class CommandEntity extends Entity {
	
	public static void register() {
		EntityRegistry.registry.put("logic_command", new EntityFactory<CommandEntity>() {	
			@Override
			public CommandEntity create(Level level, String name) {
				return new CommandEntity(level, name);
			}
		});
	}

	public CommandEntity(Level level, String name) {
		super(level, name);
	}

	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> def = new HashMap<>();
		def.put("command", new StringAttribute(""));
		def.put("executor_override", new StringAttribute(""));
		return def;
	}
	
	public Command getCommand() {
		return Command.fromString((String) getAttribute("command").getValue());
	}
	
	/**
	 * Get the Minecraft entity that should execute this command.
	 * @return The target selector, or <code>null</code> to use the calling entity.
	 */
	public TargetSelector getExecutor() {
		String str = (String) getAttribute("executor_override").getValue();
		if (str.length() > 0) {
			return TargetSelector.fromString(str);
		} else {
			return null;
		}
	}
	
	@Override
	public Collection<InputDeclaration> getDeclaredInputs() {
		Collection<InputDeclaration> in = super.getDeclaredInputs();
		in.add(new InputDeclaration() {
			
			@Override
			public String getName() {
				return "execute";
			}
			
			@Override
			public List<String> getArguements() {
				return Collections.emptyList();
			}
		});
		return in;
	}
	
	@Override
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source) {
		if (inputName.equals("execute")) {
			TargetSelector executor = getExecutor();
			if (executor != null) {
				return List.of(new ExecuteCommandBuilder().as(executor).run(getCommand()));
			} else {
				return List.of(getCommand());
			}
		}
		
		return super.compileInput(inputName, args, source);
	}
	
}
