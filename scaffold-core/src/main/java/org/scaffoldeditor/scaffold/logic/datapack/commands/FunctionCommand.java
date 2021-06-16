package org.scaffoldeditor.scaffold.logic.datapack.commands;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.nbt.util.Pair;
import org.scaffoldeditor.scaffold.logic.datapack.AbstractFunction;

/**
 * A Minecraft command that calls a function.
 * @author Igrium
 */
public class FunctionCommand implements Command {
	private String namespace;
	private String path;
	
	/**
	 * Create a function command.
	 * @param namespace Target function namespace.
	 * @param path Target function path.
	 */
	public FunctionCommand(String namespace, String path) {
		this.namespace = namespace;
		this.path = path;
	}
	
	/**
	 * Create a function command.
	 * @param target Target function metadata.
	 */
	public FunctionCommand(Pair<String, String> target) {
		this.namespace = target.getFirst();
		this.path = target.getSecond();
	}
	
	/**
	 * Create a function command.
	 * @param id Target function identifier.
	 */
	public FunctionCommand(Identifier id) {
		this.namespace = id.namespace;
		this.path = id.value;
	}
	
	/**
	 * Create a function command.
	 * @param target Target function.
	 */
	public FunctionCommand(AbstractFunction target) {
		this.namespace = target.getNamespace();
		this.path = target.getPath();
	}

	@Override
	public String compile() {
		return "function "+namespace+":"+path;
	}
}
