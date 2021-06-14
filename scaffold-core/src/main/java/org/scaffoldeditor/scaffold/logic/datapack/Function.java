package org.scaffoldeditor.scaffold.logic.datapack;

import java.util.ArrayList;
import java.util.List;

/**
 * A function compiler that simply compiles a list of commands.
 * @author Igrium
 */
public class Function extends AbstractFunction {
	
	public final List<Command> commands = new ArrayList<>();
	
	public Function(String namespace, String path) {
		super(namespace, path);
	}

	@Override
	public List<Command> getCommands() {
		return commands;
	}

}
