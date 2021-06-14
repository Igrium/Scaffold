package org.scaffoldeditor.scaffold.logic.datapack;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.scaffoldeditor.nbt.util.Pair;
import org.scaffoldeditor.nbt.util.SingleTypePair;

public final class FunctionNameUtils {
	private FunctionNameUtils() {};
	
	/**
	 * Get a function namespace and path from a function file.
	 * 
	 * @param path Path to function file, relative to the data folder
	 * @return Function namespace and pathname.
	 */
	public static SingleTypePair<String> metaFromPath(Path path) {
		Path namespacePath = path.subpath(0, 1);
		Path relativeFunctionPath = Paths.get(namespacePath.toString(), "functions").relativize(path);
		
		String namespace = namespacePath.getFileName().toString();
		String functionName = FilenameUtils.removeExtension(relativeFunctionPath.toString());
		functionName = FilenameUtils.normalize(functionName, true);
		
		return new SingleTypePair<String>(namespace, functionName);
	}
	
	/**
	 * Get a function's pathin within the data folder from it's metadata.
	 * @param meta Function meta (namespace and pathname).
	 * @return Function path.
	 */
	public static Path metaToPath(Pair<String, String> meta) {
		return Paths.get(meta.getFirst(), "functions", meta.getSecond()+".mcfunction");
	}
}
