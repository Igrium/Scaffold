package org.scaffoldeditor.scaffold.logic.datapack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;

/**
 * A function that combines the commands of a bunch of other function by copying
 * them into itself. Does not call the functions externally.
 * 
 * @author Igrium
 */
public class AggregateFunction extends AbstractFunction {
	
	public final List<AbstractFunction> functions = new ArrayList<>();

	public AggregateFunction(String namespace, String path, AbstractFunction ... functions) {
		super(namespace, path);
		this.functions.addAll(Arrays.asList(functions));
	}
	
	public AggregateFunction(Identifier id, AbstractFunction ... functions) {
		this(id.namespace, id.value, functions);
	}

	@Override
	public List<Command> getCommands() {
		List<Command> commands = new ArrayList<>();
		for (AbstractFunction function : functions) {
			commands.addAll(function.getCommands());
		}
		return commands;
	}

}
