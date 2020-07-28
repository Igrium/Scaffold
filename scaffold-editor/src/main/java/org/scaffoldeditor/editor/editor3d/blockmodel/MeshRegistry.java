package org.scaffoldeditor.editor.editor3d.blockmodel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.scaffoldeditor.editor.editor3d.EditorApp;
import org.scaffoldeditor.editor.editor3d.util.EditorUtils;

import com.rvandoosselaer.blocks.Shape;
import com.rvandoosselaer.blocks.ShapeRegistry;

/**
 * Keeps track of block meshes in relation to their JSON files.
 * @author Igrium
 */
public class MeshRegistry extends ShapeRegistry {
	public MeshRegistry() {
		super();
	}
	
	private Map<String, BlockMesh> registry = new HashMap<String, BlockMesh>() ;
	
	/**
	 * Register a block mesh.
	 * @param key Path to mesh json file relative to assets folder WITH .json extension.
	 * @param mesh Mesh to register
	 */
	public void registerMesh(String key, BlockMesh mesh) {
		registry.put(key, mesh);
		System.out.println(registry); // TESTING ONLY
	}
	
	/**
	 * Get a registered block mesh.
	 * @param key Path to mesh json file relative to assets folder WITH .json extension.
	 * @return Block mesh.
	 */
	public BlockMesh getMesh(String key) {
		return registry.get(key);
	}
	
	public Set<String> keySet() {
		return registry.keySet();
	}
	
	/**
	 * Check if the registry contains a certian mesh.
	 * @param key Path to mesh json file relative to assets folder WITH .json extension.
	 * @return Does the registry have this mesh?
	 */
	public boolean contains(String key) {
		return registry.containsKey(key);
	}
	
	/**
	 * Load a block mesh.
	 * <br>
	 * First, search the registry and see if it is already loaded.
	 * If not, read the JSON file from the JSON registry or from file and
	 * attempt to locate a parent mesh in the registry. If none exists,
	 * load it from JSON.
	 * @param key JME path file (or child of file) containing mesh to load.
	 * @return Loaded block mesh.
	 * @throws IOException If an IO exception occurs in reading the model file.
	 */
	public BlockMesh loadMesh(String key) throws IOException {
		if (contains(key)) {
			return getMesh(key);
		} else {
			JSONObject json = EditorUtils.loadJMEJson(key);
			BlockMesh mesh = BlockModelLoader.loadMesh(json, EditorApp.getInstance().getAssetManager(), key);
			registerMesh(key, mesh);
			return mesh;
		}
	}
	
	@Override
	public Shape get(String name) {
		System.out.println("Pulling from custom shape registry");
		try {
			return loadMesh(name);
		} catch (Exception e) {
			System.out.println("Cannot load mesh file "+name+". Falling back to JME Blocks shape registry.");
			e.printStackTrace();
			return super.get(name);
		}
	}
	
}
