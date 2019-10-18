package org.scaffoldeditor.scaffold.core;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This class takes care of working with paths and loading assets in the guise of the project folder
 * @author Sam54123
 *
 */
public class AssetManager {
	/* The project this asset manager is associated with */
	private Project project;
	
	public Project getProject() {
		return project;
	}
	
	/**
	 * Initialize an asset manager with a project
	 * @param project Project to initialize with
	 */
	public AssetManager(Project project) {
		this.project = project;
	}
	
	/**
	 * Returns a normalized list of the loaded paths
	 * @return Loaded paths
	 */
	public ArrayList<Path> getLoadedPaths() {
		ArrayList<Path> normalized = new ArrayList<Path>();
		ArrayList<String> unnormalized = project.gameInfo().getLoadedPaths();
		
		for (int i = 0; i < unnormalized.size(); i++) {
			normalized.add(Paths.get(normalize(unnormalized.get(i))).normalize());
		}
		
		return normalized;
		
	}
	
	/**
	 * Bake special modifiers (such as _projectfolder_) into a path
	 * @param path Path to bake
	 * @return Baked path
	 */
	public String normalize(String path) {
		String projectFolder =  project.getProjectFolder().toString();
		return path.replace("_projectfolder_", projectFolder);
	}
	
	/**
	 * Get the absolute path of a path given in relativity to the project folder
	 * @param path Relative path
	 * @return Absolute path
	 */
	public Path getAbsolutePath(String path) {
		if (Paths.get(normalize(path)).isAbsolute()) {
			return Paths.get(normalize(path));
		}
		return Paths.get(project.getProjectFolder().toString(), normalize(path));
	}
	
	/**
	 * Search in loaded paths for an asset. Returns null if asset cannot be found.
	 * @param assetPath Relative path to asset to find.
	 * @return Absolute path to asset
	 */
	public Path findAsset(String assetPath) {
		
		ArrayList<Path> loadedPaths = getLoadedPaths();
		
		// Look in order from first to last in list
		for (int i = 0; i < loadedPaths.size(); i++) {
			if (new File(loadedPaths.get(i).toString(),assetPath).exists()) { 
				return Paths.get(loadedPaths.get(i).toString(), assetPath);
			}
		}
		
		System.out.println("Unable to find asset "+assetPath);
		return null;
	}
	
}
