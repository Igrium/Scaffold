package org.scaffoldeditor.scaffold.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.scaffoldeditor.scaffold.compile.Compiler;
import org.scaffoldeditor.scaffold.io.AssetManager;
import org.scaffoldeditor.scaffold.io.AssetLoaderRegistry;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.AttributeRegistry;
import org.scaffoldeditor.scaffold.plugin_utils.DefaultPlugin;
import org.scaffoldeditor.scaffold.plugin_utils.PluginInitializer;
import org.scaffoldeditor.scaffold.plugin_utils.PluginManager;

/**
 * A Project is an object that defines all the main attributes of a project
 * @author Igrium
 *
 */
public class Project implements AutoCloseable {
	
	public static final String CACHE_FOLDER_NAME = ".scaffold";
	
	/* The project folder of this project */
	private Path projectFolder;
	
	/* The GameInfo object associated with this project */
	private GameInfo gameInfo;
	
	/* The AssetManager associated with this project */
	private AssetManager assetManager;
	
	private Compiler compiler;
	
	private PluginManager pluginManager;
	private PluginInitializer defaultPlugin;
	private final ExecutorService levelService = Executors.newSingleThreadExecutor();
	
	/**
	 * Create an empty project with an empty gameinfo
	 * @param projectFolder Folder to initialize the project in
	 */
	public Project(Path projectFolder) {
		this.projectFolder = projectFolder;
		gameInfo = new GameInfo();
		assetManager = new AssetManager(this);
		compiler = Compiler.getDefault();
		
		File cache = getCacheFolder().toFile();
		if (!cache.isDirectory()) {
			cache.mkdir();
			// If on Windows.
			if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
				try {
					Files.setAttribute(cache.toPath(), "dos:hidden", true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		pluginManager = new PluginManager();
		defaultPlugin = new DefaultPlugin();
		defaultPlugin.initialize();
	}
	
	/**
	 * Load all the plugins in the project. Can only be called once, so it
	 * obviously can't be called from a plugin.
	 */
	public void loadPlugins() {
		List<URL> urls = new ArrayList<>();
		for (String plugin : gameInfo().getPlugins()) {
			if (!FilenameUtils.getExtension(plugin).equals("jar")) {
				plugin = plugin + ".jar";
			}
			File file = projectFolder.resolve(Paths.get("plugins", plugin)).toFile();
			if (file.isFile()) {
				try {
					urls.add(file.toURI().toURL());
				} catch (MalformedURLException e) {
					throw new AssertionError(e);
				}
			}
		}
		pluginManager.loadPlugins(urls.toArray(new URL[0]));
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
		try {
			project.gameInfo = GameInfo.fromFile(Paths.get(folder, Constants.GAMEINFONAME).toFile());
		} catch (FileNotFoundException | JSONException e) {
			e.printStackTrace();
			System.out.println("Unable to load gameinfo file!");
			return null;
		}

		project.loadPlugins();
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
		project.gameInfo().getLoadedPaths().add("_projectfolder_");
		
		try {
			project.gameInfo().saveJSON(Paths.get(folder,Constants.GAMEINFONAME).toFile());
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		
		
		// Create subfolders
		try {
			Files.createDirectories(Paths.get(folder, "assets"));
			Files.createDirectories(Paths.get(folder, "data"));
			Files.createDirectories(Paths.get(folder, "game"));
			Files.createDirectories(Paths.get(folder, "maps"));
			Files.createDirectories(Paths.get(folder, "schematics"));
			Files.createDirectories(Paths.get(folder, CACHE_FOLDER_NAME));
			Files.createDirectories(Paths.get(folder,"scripts"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Unable to create folder structure!");
			return null;
		}
		project.loadPlugins();
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
	 * Get the folder intended to store temporary files (such as recently opened)
	 * without saving them to version control.
	 * 
	 * @return Path to cache folder.
	 */
	public Path getCacheFolder() {
		return projectFolder.resolve(CACHE_FOLDER_NAME);
	}
	
	/**
	 * Get the project's gameinfo
	 */
	public GameInfo gameInfo() {
		return gameInfo;
	}
	
	/**
	 * Get the project's AssetManager
	 */
	public AssetManager assetManager() {
		return assetManager;
	}
	
	/**
	 * Get the project's compiler.
	 */
	public Compiler getCompiler() {
		return compiler;
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

	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	/**
	 * Clean up any mess created by the project (entries in registries, etc)
	 */
	public void close() {
		defaultPlugin.close();
		pluginManager.closePlugins();
		
		try {
			System.out.println("Shutting down level service.");
			levelService.shutdown();
			levelService.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (!levelService.isTerminated()) {
				System.err.println("Level service failed to close in time!");
			}
			levelService.shutdownNow(); 
		}
		
		EntityRegistry.registry.clear();
		AttributeRegistry.registry.clear();
		AssetLoaderRegistry.registry.clear();
		
	}
	
	/**
	 * Get the service that should be used for most level-modifying operations. 
	 */
	public ExecutorService getLevelService() {
		return levelService;
	}
}
