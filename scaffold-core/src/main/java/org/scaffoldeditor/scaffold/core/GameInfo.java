package org.scaffoldeditor.scaffold.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * This class is responsible for loading and parsing the gameinfo file.
 * Use _projectfolder_ to represent project folder dir.
 * @author Igrium
 *
 */
public class GameInfo {
	
	/* All the loaded folders of this project */
	private List<String> loadedPaths = new ArrayList<String>();
	
	private List<String> plugins = new ArrayList<String>();
	
	/* The title of the project */
	private String title = "Default Title";
	
	/* Does this gameinfo object match the file in the directory? */
	private boolean isSaved = false;
	
	public final List<String> preCompileScripts = new ArrayList<String>();
	public final List<String> postCompileScripts = new ArrayList<String>();
	
	
	/**
	 * Set the title of the project.
	 * @param title Title
	 */
	public void setTitle(String title) {
		unpure();
		this.title = title;
	}
	
	/**
	 * Get the project's title.
	 * @return Title
	 */
	public String getTitle() {
		return title;
		
	}
	
	/**
	 * Get the absolute loaded asset paths
	 * @return Loaded paths (mutable)
	 */
	public List<String> getLoadedPaths() {
		return loadedPaths;
	}
	
	/**
	 * Get a mutable list of all this project's plugins.
	 * <br>
	 * Note: only applies once editor restarts.
	 */
	public List<String> getPlugins() {
		return plugins;
	}
	
	/**
	 * Does this GameInfo object match the gameinfo file?
	 * @return Is pure?
	 */
	public boolean isSaved() {
		return isSaved;
	}
	
	
	/**
	 * Set this gameinfo to be unpure
	 */
	public void unpure() {
		isSaved = false;
	}
	
	/**
	 * Add a folder to the project's list of loaded paths (may require restart)
	 * @param folder Path to folder to add
	 */
	public void addPath(String folder) {
		loadedPaths.add(folder);
		unpure();
	}

	
	/**
	 * Load gameinfo from a gameinfo file
	 * @param file File to load from
	 * @return Loaded GameInfo
	 * @throws FileNotFoundException If the JSON file can't be found.
	 * @throws JSONException If there's an error parsing the json file.
	 */
	public static GameInfo fromFile(File file) throws FileNotFoundException, JSONException {
		GameInfo gameInfo = new GameInfo();
		
		// Load json file from disk
		JSONObject jsonObject = null;
		JSONTokener tokener = new JSONTokener(new FileInputStream(file));
		jsonObject = new JSONObject(tokener);
		
		// Set title
		gameInfo.title = jsonObject.getString("title");

		// Load paths
		JSONArray pathsArray = jsonObject.getJSONArray("loadedFolders");
		gameInfo.loadedPaths = new ArrayList<String>();
		
		for (int i = 0; i < pathsArray.length(); i++) {
			String element = pathsArray.optString(i);
			if (element != null) {
				gameInfo.loadedPaths.add(element);
			}
		}
		
		JSONArray pluginsArray = jsonObject.getJSONArray("plugins");
		for (int i = 0; i < pluginsArray.length(); i++) {
			String element = pluginsArray.optString(i);
			if (element != null) {
				gameInfo.plugins.add(element);
			}
		}
		
		// Scripts
		JSONObject scriptsObject = jsonObject.getJSONObject("scripts");
		JSONArray preCompile = scriptsObject.optJSONArray("preCompile");
		if (preCompile != null) {
			for (Object obj : preCompile) {
				gameInfo.preCompileScripts.add(obj.toString());
			}
		}
		JSONArray postCompile = scriptsObject.optJSONArray("postCompile");
		if (postCompile != null) {
			for (Object obj : postCompile) {
				gameInfo.postCompileScripts.add(obj.toString());
			}
		}
		
		gameInfo.isSaved = true;
		
		return gameInfo;
	}
	
	/**
	 * Save this gameInfo to a json file.
	 * @param saveFile File to save to.
	 * @throws IOException If an IO exception occurs.
	 */
	public void saveJSON(File saveFile) throws IOException {
		
		JSONObject out = new JSONObject();
		out.put("title", title);
		out.put("loadedFolders", new JSONArray(loadedPaths));
		out.put("plugins", new JSONArray(plugins));
		
		JSONObject scripts = new JSONObject();
		scripts.put("preCompile", preCompileScripts);
		scripts.put("postCompile", postCompileScripts);
		out.put("scripts", scripts);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
		writer.write(out.toString(4));
		writer.flush();
		writer.close();
	}
}
