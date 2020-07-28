package org.scaffoldeditor.editor.editor3d.block;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.rvandoosselaer.blocks.TypeRegistry;


/**
 * Subclass of TypeRegistry that loads textures from file.
 * @author Igrium
 */
public class ScaffoldTypeRegistry extends TypeRegistry {
	
	protected final AssetManager assetManager;
	
	
	public ScaffoldTypeRegistry(AssetManager assetManager) {
		super(assetManager);
		this.assetManager = assetManager;
	}
	
	/**
	 * Load or get a block type.
	 * @param name JME path to texture to load.
	 */
	public Material get(String name) {
		if (getAll().contains(name)) {
			return super.get(name);
		} else {
			return loadMaterial(name);
		}
	}
	
	/**
	 * Load a material from a texture file.
	 * @param name JME path to texture to load.
	 * @return Generated material.
	 */
	protected Material loadMaterial(String name) {
		Texture tex = assetManager.loadTexture(name);
		tex.setMagFilter(MagFilter.Nearest);
		
		Material mat = assetManager.loadMaterial(DEFAULT_BLOCK_MATERIAL);
		mat.setTexture("DiffuseMap", tex);
		
		register(name, mat);
		
		return mat;
	}

}
