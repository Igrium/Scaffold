package org.scaffoldeditor.scaffold.util;

import java.io.IOException;
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
	 * @throws IOException If an IO Exceotion occurs.
	 */
	public static JSONObject loadJSON(Path file) throws IOException {
		List<String> jsonFile = Files.readAllLines(file);
		JSONObject jsonObject = new JSONObject(String.join("", jsonFile));
		return jsonObject;
	}
}
