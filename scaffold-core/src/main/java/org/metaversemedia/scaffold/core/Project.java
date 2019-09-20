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
	 * Create an empty project wckith an empty gameinfo
	 * @param projectFolder Folder to initialize the project in
	 */
	public Project(Path projectFolder) {
		this.projectFolder = projectFolder;
		gameInfo = new GameInfo();
	}

	
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
		
		Project project = new Project(Paths.get(folder));
		
		// Load the gameinfo
		project.gameInfo = GameInfo.fromFile(Paths.get(folder, Constants.gameinfoFile));
		
		if (project.gameInfo == null) {
			System.out.println("Unable to load gameinfo file!");
			return null;
		}
		
		return project;
	}
	
	/**
	 * Get the project's project folder
	 * @return Project folder
	 */
	public Path getProjectFolder() {
		return projectFolder;
	}
	
	/**
	 * Get the project's gameinfo
	 * @return gameInfo
	 */
	public GameInfo gameInfo() {
		return gameInfo;
	}
	
	
}
