package org.scaffoldeditor.scaffold.logic.datapack;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.scaffoldeditor.nbt.util.SingleTypePair;

/**
 * Represents a series of commands that can be compiled into a <code>.mcfunction</code>
 * @author Igrium
 */
public abstract class AbstractFunction {
	protected String namespace;
	protected String path;
	
	public AbstractFunction(String namespace, String path) {
		this.namespace = namespace;
		this.path = path;
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
		return namespace;
	}
	
	/**
	 * Get this function's path (the value after the colon).
	 * 
	 * @return Function Path relative to the <code>functions</code> folder,
	 *         excluding the file extension.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Set this function's namespace (the value bnefore the colon).
	 * @param namespace Function namespace.
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	/**
	 * Set this function's path (the value after the colon).
	 * 
	 * @param path Path relative to the <code>functions</code> folder,
	 *                     excluding the file extension.
	 */
	public void setPath(String path) {
		this.path = path;
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
	
	/**
	 * Retrieve this function's namespace and path.
	 * 
	 * @return Pair where the first element is the namespace and the second element
	 *         is the path.
	 */
	public SingleTypePair<String> getMeta() {
		return new SingleTypePair<>(namespace, path);
	}
}
