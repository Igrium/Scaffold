package org.metaversemedia.scaffold.core;

import java.io.File;
import java.io.IOException;
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
	 * Create a new project in a selected folder
	 * @param folder Folder to create project in
	 * @param title Pretty title for the project (used as map name)
	 * @return Newly created project (or null if creation failed)
	 */
	public static Project init(String folder, String title) {
		// Check if file already exists
		if (new File(folder,Constants.gameinfoFile).exists()) {
			System.out.println("Project already exists in "+folder+"!");
			return loadProject(folder);
		}
		
		// Create project object
		Project project = new Project(Paths.get(folder));
		
		// Setup gameinfo
		project.gameInfo().setTitle(title);
		project.gameInfo().addPath("_projectfolder_");
		project.gameInfo().addPath("_default_");
		
		if (!project.gameInfo().saveJSON(Paths.get(folder,Constants.gameinfoFile))) {
			return null;
		}
		
		
		// Create subfolders
		try {
			Files.createDirectories(Paths.get(folder,"assets"));
			Files.createDirectories(Paths.get(folder,"data"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Unable to create folder structure!");
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
