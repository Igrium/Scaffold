package org.scaffoldeditor.scaffold.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.json.JSONObject;

/**
 * Class to help with some universal json stuff.
 * @author Sam54123
 *
 */
public class JSONUtils {
	
	/**
	 * Read a JSONObject from a file.
	 * @param file File to read.
	 * @return JSONObject.
	 * @throws IOException If an IO exception occurs.
	 */
	public static JSONObject loadJSON(Path file) throws IOException {
		List<String> jsonFile = Files.readAllLines(file);
		JSONObject jsonObject = new JSONObject(String.join("", jsonFile));
		return jsonObject;
	}
	
	/**
	 * Read a JSONObject from an input stream.
	 * @param is Input stream to read.
	 * @return JSON contents.
	 * @throws IOException If an IO exception occurs.
	 * @apiNote AssetManager.loadJSON() should be used for memory caching.
	 */
	public static JSONObject loadJSON(InputStream is) throws IOException {
		BufferedReader stringReader = new BufferedReader(new InputStreamReader(is));
		StringBuilder stringBuilder = new StringBuilder();
		
		String inputStr;
		while ((inputStr = stringReader.readLine()) != null) {
			stringBuilder.append(inputStr);
		}
		
		JSONObject jsonObject = new JSONObject(stringBuilder.toString());
		is.close();
		return jsonObject;
	}

}
