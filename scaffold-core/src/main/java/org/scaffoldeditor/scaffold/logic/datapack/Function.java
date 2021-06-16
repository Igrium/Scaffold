package org.scaffoldeditor.scaffold.logic.datapack;

import java.util.ArrayList;
import java.util.List;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.nbt.util.Pair;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;

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

	@Override
	public List<Command> getCommands() {
		return commands;
	}

}
