package org.scaffoldeditor.scaffold.logic.datapack;

import org.scaffoldeditor.nbt.util.Pair;

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
		this.namespace = target.getSecond();
	}
	
	/**
	 * Create a function command.
	 * @param target Target function.
	 */
	public FunctionCommand(Function target) {
		this.namespace = target.getNamespace();
		this.path = target.getPath();
	}

	@Override
	public String compile() {
		return "function "+namespace+":"+path;
	}
}
