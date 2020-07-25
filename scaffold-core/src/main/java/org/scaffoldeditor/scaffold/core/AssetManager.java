package org.scaffoldeditor.scaffold.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.scaffoldeditor.scaffold.util.JSONUtils;

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
		loadAssetNamespaces();
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
		return project.getProjectFolder().resolve(normalize(path));
	}
	
	/**
	 * Search in loaded paths for an asset.
	 * @param assetPath Relative path to asset to find.
	 * @return Absolute path to asset. Null if asset cannot be found.
	 */
	public Path findAsset(String assetPath) {
		return findAsset(assetPath, false);
		
	}
	
	/**
	 * Search in loaded paths for an asset.
	 * @param assetPath Relative path to asset to find.
	 * @param suppressError Whether to suppress the "unable to find asset" message.
	 * @return Absolute path to asset. Null if asset cannot be found.
	 */
	public Path findAsset(String assetPath, boolean suppressError) {
		ArrayList<Path> loadedPaths = getLoadedPaths();
		
		// Look in order from first to last in list
		for (int i = 0; i < loadedPaths.size(); i++) {
			if (new File(loadedPaths.get(i).toString(),assetPath).exists()) { 
				return Paths.get(loadedPaths.get(i).toString(), assetPath);
			}
		}
		
		if (!suppressError) {
			System.out.println("Unable to find asset "+assetPath);
		}
		return null;
	}
	
	// CACHE
	
	/**
	 * Cache of loaded json objects.
	 */
	protected Map<Path, JSONObject> jsonCache = new HashMap<Path, JSONObject>();
	

	/**
	 * Load a JSON file into memory. If the file is already loaded, retrieve it from memory.
	 * @param assetPath Path to the file to load.
	 * @return Contents of JSON file.
	 * @throws IOException If an IO exception occurs.
	 */
	public JSONObject loadJSON(Path assetPath) throws IOException {
		if (jsonCache.containsKey(assetPath)) {
			return jsonCache.get(assetPath);
		}
		Path fullPath = findAsset(assetPath.toString());
		JSONObject object = JSONUtils.loadJSON(fullPath);
		jsonCache.put(assetPath, object);
		return object;
	}
	
	/**
	 * Force a JSON file into memory from an input stream. If the file is already loaded, retrieve it from memory.
	 * @param assetPath Path name to load the file under.
	 * @param is Input stream to load from.
	 * @return Contents of the JSON file
	 * @throws IOException If an IO exception occurs.
	 */
	public JSONObject loadJSON(Path assetPath, InputStream is) throws IOException {
		if (jsonCache.containsKey(assetPath)) {
			return jsonCache.get(assetPath);
		}
		JSONObject object = JSONUtils.loadJSON(is);
		jsonCache.put(assetPath, object);
		return object;
	}
	
	/**
	 * Remove a JSON file from the cache.
	 * @param assetPath File to remove.
	 * @return The previous data that was associated with the file, if any.
	 */
	public JSONObject removeJSON(Path assetPath) {
		return jsonCache.remove(assetPath);
	}
	
	/**
	 * Get a set of all the JSON assets that have been cached.
	 * @return Cached asset paths.
	 */
	public Set<Path> cachedJSON() {
		return jsonCache.keySet();
	}
	
	// ASSET LOCATOR
	
	/**
	 * All the loaded namespaces.
	 */
	protected List<String> namespaces = new ArrayList<String>();
	
	/**
	 * A map mapping resourcepack asset paths to the namespace they were found under.
	 */
	protected Map<String, String> namespaceCache = new HashMap<String, String>();
	
	/**
	 * Scan the assets folder and load any namespaces it finds.
	 */
	protected void loadAssetNamespaces() {
		namespaces.add("minecraft");
		for (Path p : getLoadedPaths()) {
			File[] directories = p.resolve("assets").toFile().listFiles(File::isDirectory);
			for (File f : directories) {
				String name = f.getName();
				if (!namespaces.contains(name)) {
					namespaces.add(name);
				}
			}
		}
	}
	
	/**
	 * Convert a resourcepack asset path to a valid Scaffold asset path.
	 * @param path Resourcepack asset path.
	 * @param namespace Namespace to use.
	 * @return Scaffold path.
	 */
	public Path convertAssetPath(String path, String namespace) {
		return Paths.get("assets", namespace, path);
	}
	
	/**
	 * Find the namespace a resourcepack file is under.
	 * @param path Path to the file (under namespace)
	 * @return File namespace.
	 */
	public String getNamespace(String path) {
		if (namespaceCache.containsKey(path)) {
			return namespaceCache.get(path);
		}
		
		for (String namespace : namespaces) {
			if (findAsset(convertAssetPath(path, namespace).toString()) != null) {
				namespaceCache.put(path, namespace);
				return namespace;
			}	
		}
		namespaceCache.put(path, "minecraft");
		return "minecraft";
	}
}
