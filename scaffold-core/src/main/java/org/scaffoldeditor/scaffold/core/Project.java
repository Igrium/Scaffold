package org.scaffoldeditor.scaffold.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.scaffoldeditor.nbt.block.BlockCollectionManager;

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
	
	/* The AssetManager associated with this project */
	private AssetManager assetManager;
	
	/**
	 * Create an empty project wckith an empty gameinfo
	 * @param projectFolder Folder to initialize the project in
	 */
	public Project(Path projectFolder) {
		this.projectFolder = projectFolder;
		gameInfo = new GameInfo();
		assetManager = new AssetManager(this);
		BlockCollectionManager.registerDefaults();
	}

	
	/**
	 * Create a new Project from a project folder
	 * @param folder Project folder
	 * @return Loaded project (or null of load failed)
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
		if (Files.notExists(Paths.get(folder, Constants.GAMEINFONAME))) {
			System.out.println(folder+" does not contain a gameinfo file!");
			return null;
		}
		
		Project project = new Project(Paths.get(folder));
		
		// Load the gameinfo
		project.gameInfo = GameInfo.fromFile(Paths.get(folder, Constants.GAMEINFONAME));
		
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
		if (new File(folder,Constants.GAMEINFONAME).exists()) {
			System.out.println("Project already exists in "+folder+"!");
			return loadProject(folder);
		}
		
		// Create project object
		Project project = new Project(Paths.get(folder));
		
		// Setup gameinfo
		project.gameInfo().setTitle(title);
		project.gameInfo().addPath("_projectfolder_");
		
		if (!project.gameInfo().saveJSON(Paths.get(folder,Constants.GAMEINFONAME))) {
			return null;
		}
		
		
		// Create subfolders
		try {
			Files.createDirectories(Paths.get(folder,"assets"));
			Files.createDirectories(Paths.get(folder,"data"));
			Files.createDirectories(Paths.get(folder,"game"));
			Files.createDirectories(Paths.get(folder,"maps"));
			
			Files.createDirectories(Paths.get(folder,"scripts"));

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
	 * @return gameInfo GameInfo
	 */
	public GameInfo gameInfo() {
		return gameInfo;
	}
	
	/**
	 * Get the project's AssetManager
	 * @return AssetManager
	 */
	public AssetManager assetManager() {
		return assetManager;
	}
	
	/**
	 * Get the project's name. (defined by the project folder's name).
	 * @return Name
	 */
	public String getName() {
		return getProjectFolder().getFileName().toString();
	}
	
	/**
	 * Get the project's title.
	 * @return Title.
	 */
	public String getTitle() {
		return gameInfo.getTitle();
	}
}
