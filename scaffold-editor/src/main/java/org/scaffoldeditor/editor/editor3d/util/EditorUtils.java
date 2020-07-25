package org.scaffoldeditor.editor.editor3d.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scaffoldeditor.editor.editor3d.EditorApp;
import org.scaffoldeditor.scaffold.core.AssetManager;
import org.scaffoldeditor.scaffold.math.Vector;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.math.Vector3f;

/**
 * A variety of utility functions to assist in the 3D section of the editor.
 * @author Sam54123
 */
public final class EditorUtils {
	/**
	 * Converts a Scaffold vector to a JMonkeyEngine vector.
	 * @param vector Scaffold vector.
	 * @return JMonkeyEngine vector.
	 */
	public static Vector3f sVectorToMVector(Vector vector) {
		return new Vector3f(vector.X(), vector.Y(), vector.Z());
	}
	
	/**
	 * Converts a JMonkeyEngine vector to a Scaffold vector.
	 * @param vector JMonkeyEngine vector.
	 * @return Scaffold vector.
	 */
	public static Vector mVectorToSVector(Vector3f vector) {
		return new Vector(vector.getX(), vector.getY(), vector.getZ());
	}
	
	/**
	 * Convert a JSON array to a JMonkeyEngine vector.
	 * <br>
	 * array[0] => x, array[1] => y, array[2] => z
	 * @param array JSON array.
	 * @return JMonkeyEngine vector.
	 */
	public static Vector3f jsonArrayToMVector(JSONArray array) {
		return new Vector3f(array.getFloat(0), array.getFloat(1), array.getFloat(2));
	}
	
	// NOTE: JME paths are identical to Scaffold paths without the assets/ prefix.
	
	/**
	 * Get a Scaffold path from a JME path.
	 * @param in JME path.
	 * @return Scaffold path.
	 */
	public static Path getScaffoldPath(String in) {
		return Paths.get("assets", in);
	}
	
	/**
	 * Get a JME path from a Scaffold path.
	 * @param in Scaffold path.
	 * @return JME path.
	 */
	public static String getJMEPath(String in) {
		return getJMEPath(Paths.get(in));

	}
	
	/**
	 * Get a JME path from a Scaffold path.
	 * @param in Scaffold path.
	 * @return JME path.
	 */
	public static String getJMEPath(Path in) {
		return Paths.get("assets").relativize(in).toString();

	}
	
	/**
	 * Load a JSON file into Scaffold's asset manager using the JME asset manager's loaded paths.
	 * @param path JME path of file.
	 * @return Loaded JSON object.
	 * @throws IOException If an IO exception occurs.
	 */
	public static JSONObject loadJMEJson(String path) throws IOException {
		AssetManager assetManager = EditorApp.getInstance().getParent().getProject().assetManager();
		Path scaffoldPath = getScaffoldPath(path);
		
		if (assetManager.cachedJSON().contains(scaffoldPath)) {
			return assetManager.loadJSON(scaffoldPath);
		}
		
		AssetInfo info = EditorApp.getInstance().getAssetManager().locateAsset(new AssetKey<>(path));	
		return assetManager.loadJSON(scaffoldPath, info.openStream());
	}
}
