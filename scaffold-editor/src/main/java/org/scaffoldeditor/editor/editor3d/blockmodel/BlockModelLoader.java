package org.scaffoldeditor.editor.editor3d.blockmodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;

public class BlockModelLoader implements AssetLoader {

	@Override
	public Geometry load(AssetInfo assetInfo) throws IOException {
		JSONObject object = loadJSON(assetInfo.openStream());
		
		return loadGeom(object, assetInfo, assetInfo.getKey().getName());
	}
	
	/**
	 * Load a block geometry.
	 * @param object JSON Object of geometry.
	 * @param name Name to give the geometry.
	 * @return Loaded geometry,
	 */
	public Geometry loadGeom(JSONObject object, AssetInfo assetInfo, String name) {
		// TODO: integrate with shape registry for optimization.
			
		JSONArray elements = object.getJSONArray("elements");
		BlockMesh mesh = new BlockMesh(elements);
		
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
