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
	 * Create a new Project from a project folder
	 * @param folder Project folder
	 * @return success
	 */
	public static Project loadProject(String folder) {
		if (folder == null) {
			return null;
		}
		
		// Make sure path exists
		if (Files.notExists(Paths.get(folder))) {
			System.out.println(folder+" does not exist!");
			return null;
		}
		
		// Check for gameinfo file
		if (Files.notExists(Paths.get(folder, Constants.gameinfoFile))) {
			System.out.println(folder+" does not contain a gameinfo file!");
			return null;
		}
		
		Project project = new Project();
		project.projectFolder = Paths.get(folder);
		
		// Load the gameinfo
		project.gameInfo = GameInfo.fromFile(Paths.get(folder, Constants.gameinfoFile));
		
		if (project.gameInfo == null) {
			System.out.println("Unable to load gameinfo file!");
			return null;
		}
		
		return project;
	}
	
	
	
	
}
