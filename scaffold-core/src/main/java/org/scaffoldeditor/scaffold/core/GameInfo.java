package org.scaffoldeditor.scaffold.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is responsible for loading and parsing the gameinfo file.
 * Use _projectfolder_ to represent project folder dir.
 * @author Igrium
 *
 */
public class GameInfo {
	
	/* All the loaded folders of this project */
	private ArrayList<String> loadedPaths = new ArrayList<String>();
	
	private List<String> plugins = new ArrayList<String>();
	
	/* The title of the project */
	private String title = "Default Title";
	
	/* Does this gameinfo object match the file in the directory? */
	private boolean isPure = false;
	
	
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
	public ArrayList<String> getLoadedPaths() {
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
	public boolean isPure() {
		return isPure;
	}
	
	
	/**
	 * Set this gameinfo to be unpure
	 */
	public void unpure() {
		isPure = false;
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
	 */
	public static GameInfo fromFile(Path file) {
		
		
		GameInfo gameInfo = new GameInfo();
		
		// Load json file from disk
		JSONObject jsonObject = null;
		try {
			jsonObject = loadJSON(file);
		} catch (JSONException e) {
			System.out.println("Invalid gameinfo.json file!");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		// Set title
		gameInfo.title = jsonObject.getString("title");
		if (gameInfo.title == null) {
			System.out.println("gameinfo.json is missing \"title\" key!");
			return null;
		}
		
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
		
		gameInfo.isPure = true;
		
		return gameInfo;
	}
	
	/**
	 * Save this gameInfo to a json file
	 * @param saveFile File to save to
	 * @return Success
	 */
	public boolean saveJSON(Path saveFile) {
		
		/* Use a custom write script for nice formatting */
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile.toString()));
			
			// Write title
			writer.write("{");
			writer.newLine();
			writer.write("    \"title\":\""+title+"\",");
			writer.newLine();
			
			// Write loaded folders
			writer.write("    \"loadedFolders\":[");
			writer.newLine();
			for (int i = 0; i < loadedPaths.size(); i++) {
				writer.write("        \""+loadedPaths.get(i)+"\"");
				if (i != loadedPaths.size() - 1) {
					writer.write(",");
				}
				writer.newLine();
			}
			writer.write("    ].");
			
			writer.newLine();
			writer.write(
					  "   \"plugins\": [" + System.lineSeparator()
					+ "        " + System.lineSeparator()
					+ "    ]" + System.lineSeparator());
			
			writer.write("}");
			
			writer.flush();
			writer.close();
			
			isPure = true;
			return true;
		} catch (IOException e) {
			System.out.println("Unable to save gameinfo.json!");
			e.printStackTrace();
			return false;
		}
	}
	
	private static JSONObject loadJSON(Path inputPath) throws IOException, JSONException {
		
		List<String> jsonFile = Files.readAllLines(inputPath);
		
		JSONObject jsonObject = new JSONObject(String.join("", jsonFile));
		
		return jsonObject;
	}
}
