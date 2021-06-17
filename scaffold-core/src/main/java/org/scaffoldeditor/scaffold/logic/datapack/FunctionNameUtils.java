package org.scaffoldeditor.scaffold.logic.datapack;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.scaffoldeditor.nbt.util.Identifier;

public final class FunctionNameUtils {
	private FunctionNameUtils() {};
	
	/**
	 * Get a function namespace and path from a function file.
	 * 
	 * @param path Path to function file, relative to the data folder
	 * @return Function namespace and pathname.
	 */
	public static Identifier identifierFromPath(Path path) {
		Path namespacePath = path.subpath(0, 1);
		Path relativeFunctionPath = Paths.get(namespacePath.toString(), "functions").relativize(path);
		
		String namespace = namespacePath.getFileName().toString();
		String functionName = FilenameUtils.removeExtension(relativeFunctionPath.toString());
		functionName = FilenameUtils.normalize(functionName, true);
		
		return new Identifier(namespace, functionName);
	}
	
	/**
	 * Get a function's path within the data folder from its identifier.
	 * @param id Function identifier.
	 * @return Function path relative to the data folder.
	 */
	public static Path identifierToPath(Identifier id) {
		return Paths.get(id.namespace, "functions", id.value+".mcfunction");
	}
}
