package org.scaffoldeditor.editor.editor3d.test;

import java.io.IOException;
import java.nio.file.Path;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scaffoldeditor.editor.editor3d.EditorApp;
import org.scaffoldeditor.editor.editor3d.blockmodel.BlockMesh;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.util.JSONUtils;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;

/**
 * The tester is a place to put 3d view test methods without congesting the code.
 */
public class Tester {
	/**
	 * Run the test code.
	 * @param app App to run it on.
	 */
	public static void test(EditorApp app) {
		// Load and display test model.
		Project project = app.getParent().getProject();
		
		System.out.println(project.assetManager().getLoadedPaths());
		Path modelPath = project.assetManager().findAsset("assets/testModel.json");
		System.out.println("loading model: "+modelPath);
		
		try {
			JSONObject modelObj = JSONUtils.loadJSON(modelPath);
			
			JSONArray elements = modelObj.getJSONArray("elements");
			BlockMesh mesh = new BlockMesh(elements);
			
			// Apply to a geometry
			Geometry model = new Geometry("model", mesh);
			
			Material mat1 = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
			Texture tex = app.getAssetManager().loadTexture("testTexture.png");
			tex.setMagFilter(MagFilter.Nearest);
			mat1.setTexture("ColorMap", tex);
			model.setMaterial(mat1);
			
			app.getRootNode().attachChild(model);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
