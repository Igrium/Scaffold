package org.metaversemedia.scaffold.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * A Project is an object that defines all the main attributes of a project
 * @author Sam54123
 *
 */
public class Project {
	
	/* The project folder of this project */
	private Path projectFolder;
	
	/* The GameInfo object associated with this project */
	private GameInfo gameInfo;

	
	/**
	 * Create a new Project with a Path object
	 * @param folder Project folder
	 * @return success
	 */
	public boolean ProjectFolder(String folder) {
		if (folder == null) {
			return false;
		}
		
		// Make sure path exists
		if (Files.notExists(Paths.get(folder))) {
			System.out.println(folder+" does not exist!");
			return false;
		}
		
		// Check for gameinfo file
		if (Files.notExists(Paths.get(folder, Constants.gameinfoFile))) {
			System.out.println(folder+" does not contain a gameinfo file!");
			return false;
		}
		
		// Load the gameinfo
		gameInfo = GameInfo.fromFile(Paths.get(folder, Constants.gameinfoFile));
		
		if (gameInfo == null) {
			System.out.println("Unable to load gameinfo file!");
			return false;
		}
		
		return true;
	}
	
	
}
