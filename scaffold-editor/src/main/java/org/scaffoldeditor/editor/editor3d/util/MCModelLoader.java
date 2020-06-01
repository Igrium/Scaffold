package org.scaffoldeditor.editor.editor3d.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.scaffoldeditor.scaffold.core.Project;


/**
 * Asset loader responsible for loading Minecraft model files
 * @author Sam54123
 * @deprecated Use BlockMesh
 */
public class MCModelLoader {
	private Project project;
	
	/**
	 * Get the project the model loader is associated with
	 * @return Project
	 */
	public Project getProject() {
		return project;
	}
	
	/**
	 * Create a new model loader/
	 * @param project Project to associate with.
	 */
	public MCModelLoader(Project project) {
		this.project = project;
	}
	
	/**
	 * Collapse the parent JSON files of a model into one JSON file.
	 * @param object Object to collapse.
	 * @return Collapsed object.
	 */
	protected JSONObject collapseParents(JSONObject object) {
		
		// Load parent
		JSONObject parent = null;
		
		if (object.has("parent")) {
			try {
				Path parentPath = project.assetManager().findAsset(
						object.getString("parent"));
				JSONObject parentJSON = loadJSON(parentPath);
				parent = collapseParents(parentJSON);
				
			} catch (JSONException | IOException e) {
				e.printStackTrace();
				System.out.println("Unable to load model parent: "+object.getString("parent"));
			}
		}
		
		JSONObject finalObject = new JSONObject();
		
		if (object.has("elements")) {
			finalObject.put("elements", object.get("elements"));
		} else if (parent != null && parent.has("elements")) {
			finalObject.put("elements", parent.get("elements"));
		}			 
		
		if (object.has("textures")) {
			finalObject.put("textures", object.get("textures"));
		} else if (parent != null && parent.has("textures")) {
			finalObject.put("textures", parent.get("textures"));
		}
		
		if (object.has("display")) {
			finalObject.put("display", object.get("display"));
		} else if (parent != null && parent.has("display")) {
			finalObject.put("display", parent.get("display"));
		}
		
		if (object.has("ambientocclusion")) {
			finalObject.put("ambientocclusion", object.get("ambientocclusion"));
		} else if (parent != null && parent.has("ambientocclusion")) {
			finalObject.put("ambientocclusion", parent.get("ambientocclusion"));
		}
		
		return finalObject;
	}
	
	/* Load a JSONObject from a file */
	private static JSONObject loadJSON(Path inputPath) throws IOException, JSONException {
		List<String> jsonFile = Files.readAllLines(inputPath);
		JSONObject jsonObject = new JSONObject(String.join("", jsonFile));
		return jsonObject;
	}
}
