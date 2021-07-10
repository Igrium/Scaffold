package org.scaffoldeditor.scaffold.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.core.Project;

/**
 * This class takes care of working with paths and loading assets in the guise of the project folder
 * @author Igrium
 *
 */
public class AssetManager {
	/* The project this asset manager is associated with */
	private Project project;
	
	private static AssetManager instance;
	
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
		instance = this;
	}
	
	/**
	 * While AssetManager isn't <i>technically</i> a singleton, there will ususally only be
	 * one instance active at a time. This is simply a utility method to obtain the most
	 * recently created instance.
	 */
	public static AssetManager getInstance() {
		return instance;
	}
	
	/**
	 * Check weather an asset exists within the primary project folder.
	 * 
	 * @param in Asset path.
	 * @return Whether it's writable. Always <code>true</code> if the asset doesn't exist.
	 */
	public boolean isWritable(String in) {
		File file = project.getProjectFolder().resolve(in).toFile();
		return file.exists() || getAsset(in) == null;
	}

	/**
	 * Search all the loaded asset sources for a file. <br>
	 * Begins by searching the project folder and other search directories. If the
	 * file is not found there, it searches Scaffold's builtin resources. Finally,
	 * it searches the resources of plugins in the order defined in
	 * <code>gameinfo.json</code>.
	 * 
	 * @param in Pathname of file to find relative to project root.
	 * @return URL of the located file or null if it doesn't exist. May be a file or
	 *         a reference to a file within a jar.
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
		
		return project.getPluginManager().getClassLoader().getResource(in);
	}
	
	/**
	 * Get a list of all instances of an asset in search order, where assets earlier in the list are prioritized.
	 * Follows the search order defined in {@link #getAsset(String)}.
	 * @param in Asset to search for.
	 * @return All instances of the asset.
	 * @throws IOException If an IO exception occurs.
	 */
	public List<URL> getAssets(String in) throws IOException {
		List<URL> assets = new ArrayList<>();
		
		for (Path folder : searchDirectories) {
			File file = folder.resolve(in).toFile();
			if (file.isFile()) {
				try {
					assets.add(file.toURI().toURL());
				} catch (MalformedURLException e) {
					throw new AssertionError();
				}
			}
		}
		
		assets.addAll(Collections.list(project.getPluginManager().getClassLoader().getResources(in)));
		return assets;
	}
	
	/**
	 * Get a file as an asset path relative to the project folder.
	 * @param file File to relativise.
	 * @return Local asset path. As with all asset paths, uses '/' as a seperator.
	 */
	public String relativise(File file) {
		String path = project.getProjectFolder().relativize(file.toPath()).toString();
		return path.replace("\\", "/"); // Some systems don't play nice with backslashes.
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
			Object asset = AssetLoaderRegistry.loadAsset(is, FilenameUtils.getExtension(in));
			cache.put(in, asset);
			return asset;
		} catch (Exception e) {
			throw new IOException("Unable to load asset: "+in, e);
		}

	}
	
	/**
	 * Get an absolute file of an asset path.
	 * <br>
	 * <b>Note:</b> This should not be used for obtaining references to existing assets.
	 * For that use {@link #getAsset} or {@link #loadAsset}. It is intended as a utility
	 * function for identifying files to save.
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
	 * Force-enter an object into the cache.
	 * <br>
	 * <b>WARNING</b> This is very dangerous and is intended only to be used when 
	 * an asset has been written out to file and has a high likelyhood of being read
	 * again.
	 * @param entry Supposed cache to asset.
	 * @param value Asset value to cache.
	 */
	public void forceCache(String entry, Object value) {
		String ext = FilenameUtils.getExtension(entry);
		if (!getLoader(entry).assetClass.isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException(
					"A value was attempted forced into the asset cache under the wrong filetype. ." + ext
							+ " is not compable with " + value.getClass().getCanonicalName());
		}
		cache.put(entry, value);
	}
	
	/**
	 * Get the asset loader that would be used to load an asset.
	 */
	public AssetLoader<?> getLoader(String asset) {
		return AssetLoaderRegistry.registry.get(FilenameUtils.getExtension(asset));
	}
	
	/**
	 * Utility function to load a text file as a string.
	 * 
	 * @param asset Asset to load. Must have a registered asset loader that returns
	 *              a value assignable to {@code String}!
	 * @return Loaded string.
	 */
	public String loadText(String asset) {
		if (!getLoader(asset).isAssignableTo(String.class)) {
			throw new IllegalArgumentException(asset+" is not loadable as a string!");
		}
		
		try {
			return (String) loadAsset(asset, false);
		} catch (IOException e) {
			LogManager.getLogger().error("Error loading text file!", e);
			return "";
		}
	}
}
