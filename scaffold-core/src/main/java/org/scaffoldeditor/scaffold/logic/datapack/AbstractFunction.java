package org.scaffoldeditor.scaffold.logic.datapack;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;

/**
 * Represents a series of commands that can be compiled into a <code>.mcfunction</code>
 * @author Igrium
 */
public abstract class AbstractFunction {
	protected Identifier id;
	
	public AbstractFunction(String namespace, String path) {
		this.id = new Identifier(namespace, path);
	}
	
	public AbstractFunction(Identifier id) {
		this.id = id;
	}
	
	/**
	 * Get all of the commands in this function in order.
	 * @return
	 */
	public abstract List<Command> getCommands();
	
	/**
	 * Get this function's namespace (the value before the colon).
	 * @return Function namespace.
	 */
	public String getNamespace() {
		return id.namespace;
	}
	
	/**
	 * Get this function's path (the value after the colon).
	 * 
	 * @return Function Path relative to the <code>functions</code> folder,
	 *         excluding the file extension.
	 */
	public String getPath() {
		return id.value;
	}
	
	/**
	 * Set this function's namespace (the value bnefore the colon).
	 * @param namespace Function namespace.
	 */
	public void setNamespace(String namespace) {
		this.id = new Identifier(namespace, id.value);
	}
	
	/**
	 * Set this function's path (the value after the colon).
	 * 
	 * @param path Path relative to the <code>functions</code> folder,
	 *                     excluding the file extension.
	 */
	public void setPath(String path) {
		this.id = new Identifier(id.namespace, path);
	}
	
	/**
	 * Set this function's identifier.
	 */
	public void setIdentifier(Identifier id) {
		this.id = id;
	}
	
	/**
	 * Compile this function.
	 * @return <code>.mcfunction</code> file data.
	 */
	public String compile() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		
		for (Command command : getCommands()) {
			writer.println(command.compile());
		}
		
		return stringWriter.toString();
	}
	
	public Identifier getID() {
		return id;
	}
	
	@Override
	public String toString() {
		return id.toString();
	}
}
