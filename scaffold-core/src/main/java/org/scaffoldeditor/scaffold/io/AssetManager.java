package org.scaffoldeditor.scaffold.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.scaffoldeditor.scaffold.core.Project;

/**
 * This class takes care of working with paths and loading assets in the guise of the project folder
 * @author Igrium
 *
 */
public class AssetManager {
	/* The project this asset manager is associated with */
	private Project project;
	
	/**
	 * Directories to search for assets in.
	 * Directories lower in the list are prioritized.
	 */
	public final List<Path> searchDirectories = new ArrayList<>();
	private Map<String, Object> cache = new HashMap<>();
	
	public Project getProject() {
		return project;
	}
	
	/**
	 * Initialize an asset manager with a project
	 * @param project Project to initialize with
	 */
	public AssetManager(Project project) {
		this.project = project;
		searchDirectories.add(project.getProjectFolder());
	}
	
	/**
	 * Search all the loaded asset sources for a file.
	 * <br>
	 * Begins by searching the project folder and other search directories. If the file is not found there,
	 * it searches the resources of plugins in the order defined in <code>gameinfo.json</code>.
	 * Finally, it draws on Scaffold's built-in resources.
	 * @param in Pathname of file to find relative to project root.
	 * @return URL of the located file or null if it doesn't exist. May be a file or a reference to a file within a jar.
	 */
	public URL getAsset(String in) {
		for (Path folder : searchDirectories) {
			File file = folder.resolve(in).toFile();
			if (file.isFile()) {
				try {
					return file.toURI().toURL();
				} catch (MalformedURLException e) {
					throw new AssertionError(e);
				}
			}
		}
		
		URL resourceURL = project.getPluginManager().getClassLoader().getResource(in);
		if (resourceURL != null) {
			return resourceURL;
		}
		
		return getClass().getClassLoader().getResource(in);		
	}
	
	/**
	 * Find an asset and open it's input stream. Follows the rules described in {@link #getAsset(String) getAsset}.
	 * @param in Pathname of the file to find relative to the project root.
	 * @return The input stream.
	 * @throws IOException If an IO exception occurs.
	 */
	public InputStream getAssetAsStream(String in) throws IOException {
		URL url = getAsset(in);
		if (url == null) {
			throw new FileNotFoundException("Unable to find asset: "+in);
		}
		return url.openStream();
	}
	
	/**
	 * Locate an asset and load it using the asset type registry.
	 * @param in Pathname of the file to load relative to the project root.
	 * @param force If true, we will ignore the cache and reload from file.
	 * @return Loaded asset.
	 * @throws IOException If an IO exception occurs.
	 */
	public Object loadAsset(String in, boolean force) throws IOException {
		if (!force && cache.containsKey(in)) {
			return cache.get(in);
		}
		InputStream is = getAssetAsStream(in);
		try {
			Object asset = AssetTypeRegistry.loadAsset(is, FilenameUtils.getExtension(in));
			cache.put(in, asset);
			return asset;
		} catch (Exception e) {
			throw new IOException("Unable to load asset: "+in, e);
		}

	}
	
	/**
	 * Get an absolute file path.
	 * @param in Pathname of the file, which may or may not be relative to the project root.
	 * @return Absolute file path.
	 */
	public File getAbsoluteFile(String in) {
		File file = new File(in);
		if (file.isAbsolute()) {
			return file;
		} else {
			return project.getProjectFolder().resolve(in).toFile();
		}
	}
	
	public Path getAbsolutePath(String in) {
		return getAbsoluteFile(in).toPath();
	}
	
	public void clearCache() {
		cache.clear();
	}
	
	/**
	 * Get the asset loader that would be used to load an asset.
	 */
	public AssetType<?> getLoader(String asset) {
		return AssetTypeRegistry.registry.get(FilenameUtils.getExtension(asset));
	}
}
