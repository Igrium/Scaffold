package org.scaffoldeditor.scaffold.logic;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class represents the dataignore.txt file used when compiling datapack
 * @author Sam54123
 *
 */
public final class DataIgnore extends FileIgnore {

	public DataIgnore(Path path) {
		super(path);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addDefaults() {
		ignoredFiles.add(Paths.get("dataignore.txt"));
		ignoredFiles.add(Paths.get("compile.json"));
	}

}
