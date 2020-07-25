package org.scaffoldeditor.editor.editor3d.blockmodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scaffoldeditor.editor.editor3d.EditorApp;
import org.scaffoldeditor.editor.editor3d.util.EditorUtils;
import org.scaffoldeditor.scaffold.core.AssetManager;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;

// This is spaghetti code. I hope to revisit it later.
public class BlockModelLoader implements AssetLoader {
	
	AssetManager scaffoldAssetManager;
	Path scaffoldName;

	@Override
	public Geometry load(AssetInfo assetInfo) throws IOException {
		scaffoldAssetManager = EditorApp.getInstance().getParent().getProject().assetManager();
		scaffoldName = EditorUtils.getScaffoldPath(assetInfo.getKey().getName());
		
		// Store all loaded JSON in the master scaffold editor manager in case other code calls on it.
		JSONObject object = scaffoldAssetManager.loadJSON(scaffoldName, assetInfo.openStream());
		
		return loadGeom(object, assetInfo, assetInfo.getKey().getName());
	}
	
	/**
	 * Load a block geometry.
	 * @param object JSON Object of geometry.
	 * @param name Name to give the geometry.
	 * @return Loaded geometry,
	 * @throws IOException If an IO exception occurs in parent finding.
	 */
	public Geometry loadGeom(JSONObject object, AssetInfo assetInfo, String name) throws IOException {
		// TODO: integrate with shape registry for optimization.
		
		BlockMesh mesh = loadMesh(object, assetInfo.getManager(), assetInfo.getKey().getName());
		
		Geometry geom = new Geometry(name, mesh);
		
		// Load texture. TODO: Support multiple textures per model.
		JSONObject textures = object.optJSONObject("textures");
		Material mat = new Material(assetInfo.getManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		
		if (textures != null) {
			String texString = textures.getString("0");
			Texture tex = assetInfo.getManager().loadTexture("textures/"+texString+".png");
			tex.setMagFilter(MagFilter.Nearest);
			mat.setTexture("ColorMap", tex);
		} else {
			mat.setColor("Color", ColorRGBA.Red);
		}
		
		geom.setMaterial(mat);
		
		return geom;
	}
	
	/**
	 * Recursively traverse parents until a model with an elements tag appears and then use it.
	 * @param object Full model JSON object.
	 * @param assetManager The project JME asset manager (used to search for parents)
	 * @param path JME path of current JSON file. (for identification in the mesh registry)
	 * @return Loaded blockmesh.
	 * @throws IOException If an IO exception occurs in parent finding.
	 */
	public static BlockMesh loadMesh(JSONObject object, com.jme3.asset.AssetManager assetManager, String path) throws IOException {
		AssetManager scaffoldAssetManager = EditorApp.getInstance().getParent().getProject().assetManager();
		
		if (object.has("elements")) {
			// Check mesh registry for existing mesh.
			MeshRegistry registry = EditorApp.getInstance().getMeshRegistry();
			if (registry.contains(path)) {
				return registry.getMesh(path);
			} else {
				JSONArray elements = object.getJSONArray("elements");
				BlockMesh mesh = new BlockMesh(elements);
				registry.registerMesh(path, mesh);
				return mesh;
			}
			
			
		} else if (object.has("parent")) {
			// Get JME path of parent.
			String parent = object.getString("parent");
			String parentNamespace = scaffoldAssetManager.getNamespace("models/"+parent);
			String parentPath = Paths.get(parentNamespace, "models", parent).toString();
			
			JSONObject parentJSON = EditorUtils.loadJMEJson(parentPath);
			
			return loadMesh(parentJSON, assetManager, parentPath);
			
		} else {
			throw new IllegalArgumentException("Minecraft model must have an elements or a parent tag.");
		}
	}
	
	protected static JSONObject loadJSON(InputStream is) throws IOException {
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
