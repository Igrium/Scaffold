package org.metaversemedia.scaffold.core;

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
	
	private static JSONObject loadJSON(Path inputPath) throws IOException, JSONException {
		
		List<String> jsonFile = Files.readAllLines(inputPath);
		JSONObject animation = new JSONObject(String.join("", jsonFile));
		
		return animation;
	}
}
