package org.metaversemedia.scaffold.editor.editor3d;

import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.level.entity.Entity.RenderType;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;

/**
 * Visual representation of an Entity in the 3D scene
 * @author Sam54123
 */
public class Entity3D extends Node {
	
	private Entity entity;
	private EditorApp app;
	
	private String assetPath;
	
	public Entity3D(Entity entity, EditorApp app) {
		this.entity = entity;
		this.app = app;
		
		refresh();
	}
	
	/**
	 * Get the Entity this Entity3D represents.
	 * @return Represented entity.
	 */
	public Entity getEntity() {
		return entity;
	}
	
	/**
	 * Refresh the visual entity with values from the represented entity.
	 */
	public void refresh() {
		if (entity == null) {
			return;
		}
		
		if (assetPath != entity.getRenderAsset()) {
			if (entity.getRenderType() == RenderType.MODEL) {
				// TODO implement this.
			} else {		
				Quad q = new Quad(1,1);
				Geometry geom = new Geometry("billboard", q);
				
				// Setup billboard material.
				Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
				Texture tex = app.getAssetManager().loadTexture(entity.getRenderAsset());
				tex.setMagFilter(MagFilter.Nearest);
				mat.setTexture("ColorMap", tex);
				geom.setMaterial(mat);
				
				this.attachChild(geom);
			}
		}
	}
}
