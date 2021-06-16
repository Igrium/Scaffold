package org.scaffoldeditor.scaffold.logic.datapack;

import java.util.ArrayList;
import java.util.List;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.nbt.util.Pair;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;

/**
 * A function compiler that simply compiles a list of commands.
 * @author Igrium
 */
public class Function extends AbstractFunction {
	
	public final List<Command> commands = new ArrayList<>();
	
	public Function(String namespace, String path) {
		super(namespace, path);
	}

	public Function(Pair<String, String> meta) {
		super(meta.getFirst(), meta.getSecond());
	}
	
	public Function(Identifier id) {
		super(id.namespace, id.value);
	};
	
	/**
	 * Add a block of commands that will all be run under an Execute command.
	 * The execute command is duplicated for every command passed. This is just
	 * a utility function to create less typing.
	 * @param builder Execute command builder to use.
	 * @param commands Commands to add.
	 */
	public void addExecuteBlock(ExecuteCommandBuilder builder, List<Command> commands) {
		for (Command command : commands) {
			this.commands.add(builder.run(command));
		}
	}

	@Override
	public List<Command> getCommands() {
		return commands;
	}

}
