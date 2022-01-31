package org.scaffoldeditor.scaffold.entity.logic;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.EntityFactory;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

/**
 * Executes a single Minecraft command when compiled.
 * @author Igrium
 */
public class CommandEntity extends LogicEntity {
	
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

	@Attrib
	protected StringAttribute command = new StringAttribute("");
	
	@Attrib(name = "executor_override")
	protected StringAttribute executorOverride = new StringAttribute("");

	
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
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source, Entity instigator) {
		if (inputName.equals("execute")) {
			TargetSelector executor = getExecutor();
			if (executor != null) {
				return List.of(new ExecuteCommandBuilder().as(executor).run(getCommand()));
			} else {
				return List.of(getCommand());
			}
		}
		
		return super.compileInput(inputName, args, source, instigator);
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/command.png";
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/logic_command.sdoc", super.getDocumentation());
	}
}
