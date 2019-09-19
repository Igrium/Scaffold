package org.metaversemedia.scaffold.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is responsible for loading and parsing the gameinfo file.
 * @author Sam54123
 *
 */
public class GameInfo {
	
	/* All the loaded folders of this project */
	private ArrayList<String> loadedPaths;
	
	/* The title of the project */
	private String title = "DefaultTitle";
	
	/* Load gameinfo from a gameinfo file */
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
		JSONArray pathsArray = jsonObject.getJSONArray("paths");
		gameInfo.loadedPaths = new ArrayList<String>();
		
		for (int i = 0; i < pathsArray.length(); i++) {
			String element = pathsArray.getString(i);
			if (element != null && Files.exists(Paths.get(element))) {
				gameInfo.loadedPaths.add(element);
			}
		}
		
		return gameInfo;
	}
	
	/**
	 * Save this gameInfo to a json file
	 * @param saveFile File to save to
	 */
	public boolean saveJSON(Path saveFile) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile.toString()));
			
			// Write title
			writer.write("{");
			writer.newLine();
			writer.write("    \"title\":\""+title+"\"");
			writer.newLine();
			writer.newLine();
			
			// Write loaded folders
			writer.write("    \"loadedFolders\":[");
			writer.newLine();
			for (int i = 0; i < loadedPaths.size(); i++) {
				writer.write("        \""+loadedPaths.get(i)+"\"");
				if (i != loadedPaths.size() - 1) {
					writer.write(",");
				}
			}
			writer.write(    "]");
			
			writer.newLine();
			writer.write("}");
			
			writer.flush();
			writer.close();
			
			return true;
		} catch (IOException e) {
			System.out.println("Unable to save gameinfo.json!");
			e.printStackTrace();
			return false;
		}
	}
	
	private static JSONObject loadJSON(Path inputPath) throws IOException, JSONException {
		
		List<String> jsonFile = Files.readAllLines(inputPath);
		JSONObject animation = new JSONObject(String.join("", jsonFile));
		
		return animation;
	}
}
