package org.scaffoldeditor.editor.editor3d.blockmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.scaffoldeditor.editor.editor3d.EditorApp;
import org.scaffoldeditor.editor.editor3d.util.EditorUtils;

import com.rvandoosselaer.blocks.Shape;
import com.rvandoosselaer.blocks.ShapeRegistry;

/**
 * Keeps track of block meshes in relation to their JSON files.
 * <br>
 * Each entry consists of a base mesh and 3 rotational variants.
 * To access the rotational variants using the get method,
 * append a <code>;</code> to the key, followed by an integer from 0-3
 * representing the rotation to access.
 * @author Igrium
 */
public class MeshRegistry extends ShapeRegistry {
	
	/**
	 * Represents an entry in the mesh registry.
	 * @author Igrium
	 */
	public static class MeshEntry {
		public MeshEntry(BlockMesh mesh) {
			master = mesh;
		}
		
		private BlockMesh master = null;
		private BlockMesh rot1 = null;
		private BlockMesh rot2 = null;
		private BlockMesh rot3 = null;
		
		public BlockMesh getRotation(int rot) {
			if (rot == 1) {
				if (rot1 == null) {
					rot1 = master.getRotatedCopy(1);
				}
				return rot1;
			}
			
			if (rot == 2) {
				if (rot2 == null) {
					rot2 = master.getRotatedCopy(1);
				}
				return rot2;
			}
			
			if (rot == 3) {
				if (rot3 == null) {
					rot3 = master.getRotatedCopy(1);
				}
				return rot3;
			}
			
			return master;
		}
		
	}
	
	public static final EmptyBlockMesh EMPTYBLOCKMESH = new EmptyBlockMesh();
	
	public MeshRegistry() {
		super();
	}
	
	private Map<String, MeshEntry> registry = new HashMap<String, MeshEntry>();
	
	/**
	 * List of keys which are known to fail loading. Used to prevent the repeat attempted loading of non-working keys.
	 */
	protected List<String> blacklist = new ArrayList<String>();
	
	/**
	 * Register a block mesh.
	 * @param key Path to mesh json file relative to assets folder WITH .json extension.
	 * @param mesh Mesh to register
	 */
	public void registerMesh(String key, BlockMesh mesh) {
		registry.put(key, new MeshEntry(mesh));
	}
	
	/**
	 * Register a block mesh with an existing mesh entry.
	 * @param key Path to mesh json file relative to assets folder WITH .json extension.
	 * @param entry Mesh entry to register.
	 */
	public void registerMesh(String key, MeshEntry entry) {
		registry.put(key, entry);
	}
	
	/**
	 * Get a registered block mesh.
	 * @param key Path to mesh json file relative to assets folder WITH .json extension.
	 * @param rotation Value from 0-3 representing the rotation of the mesh to get.
	 * @return Block mesh.
	 */
	public BlockMesh getMesh(String key, int rotation) {
		return registry.get(key).getRotation(rotation);
	}
	
	/**
	 * Get a registered block mesh.
	 * @param key Path to mesh json file relative to assets folder WITH .json extension.
	 * @return Block mesh.
	 */
	public BlockMesh getMesh(String key) {
		return getMesh(key, 0);
	}
	
	
	public Set<String> keySet() {
		return registry.keySet();
	}
	
	/**
	 * Check if the registry contains a certain mesh.
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
	 * @param rotation A value from 0-3 representing the rotation of the mesh to return.
	 * @return Loaded block mesh.
	 * @throws IOException If an IO exception occurs in reading the model file.
	 */
	public BlockMesh loadMesh(String key, int rotation) throws IOException {
		if (contains(key)) {
			return getMesh(key, rotation);
		} else {
			JSONObject json = EditorUtils.loadJMEJson(key);
			BlockMesh mesh = BlockModelLoader.loadMesh(json, EditorApp.getInstance().getAssetManager(), key);
			registerMesh(key, mesh);
			return getMesh(key, rotation);
		}
	}
	
	public BlockMesh loadMesh(String key) throws IOException {
		return loadMesh(key, 0);
	}
	
	/**
	 * Attempt to obtain an entry from the mesh registry,
	 * and if that fails, fall back on the default JME blocks 
	 * shape registry.
	 * <br>
	 * To access the rotational variants using the get method,
	 * append a '<code>;</code>' to the key, followed by an integer from 0-3
	 * representing the rotation to access.
	 * @author Igrium
	 */
	@Override
	public Shape get(String name) {
		String[] key = name.split(";", 2);
		
		if (blacklist.contains(key[0])) {
			return loadBackup(name);
		}
		
		try {

			return key.length > 1 ? loadMesh(key[0], Integer.valueOf(key[1])) : loadMesh(key[0]);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Cannot load mesh file "+name+". Falling back to JME Blocks shape registry.");
			
			blacklist.add(key[0]);
			return loadBackup(name);
		}
	}
	
	/**
	 * Called when the loading of a mesh has failed and we must fall back to the JME Blocks shape registry.
	 * @param name Shape key.
	 * @return Shape to render.
	 */
	protected Shape loadBackup(String name) {
		Shape shape = super.get(name);
		if (shape == null) {
			return EMPTYBLOCKMESH;
		} else {
			return shape;
		}
	}
}
