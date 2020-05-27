package org.scaffoldeditor.editor.editor3d;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.editor.ui.EditorWindow;
import org.scaffoldeditor.scaffold.level.entity.Entity;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.scene.Spatial;
import com.rvandoosselaer.blocks.BlocksConfig;

/**
 * The 3d rendered part of the editor.
 * @author Sam54123
 */
public class EditorApp extends SimpleApplication {
	
	private EditorWindow parent;
	
	// Maps entity3ds to their entity counterparts.
	private Map<Entity, Entity3D> entities = new HashMap<Entity, Entity3D>();
	
	/**
	 * Get this app's parent window.
	 * @return Parent.
	 */
	public EditorWindow getParent() {
		return parent;
	}
	
	/**
	 * Create an editor app.
	 * @param parent Parent window.
	 */
	public EditorApp(EditorWindow parent) {
		this.parent = parent;
	}
	
	
	@Override
	public void simpleInitApp() {
		// activate windowed input behavior
		flyCam.setDragToRotate(true);
		flyCam.setMoveSpeed(10);
		
		// load assets
		org.scaffoldeditor.scaffold.core.AssetManager scaffoldAssetManager = parent.getProject().assetManager();
		for (Path p : scaffoldAssetManager.getLoadedPaths()) {
			getAssetManager().registerLocator(p.resolve("assets").toString(), FileLocator.class);
		}
		
		getAssetManager().registerLocator("assets", ClasspathLocator.class); // Default assets
		
		BlocksConfig.initialize(assetManager);
		reload();
	}
	
	/**
	 * Reload the level from scratch.
	 */
	public void reload() {
		restart();
		for (Spatial n : rootNode.getChildren()) {
			n.removeFromParent();
		}
		
		entities.clear();
		
		
		// Add all entities
		for (Entity ent : parent.getLevel().getEntities().values()) {
			Entity3D ent3d = new Entity3D(ent, this);
			entities.put(ent, ent3d);
			rootNode.attachChild(ent3d);
		}

	}
	
	/**
	 * Refresh the visual element of an entity.
	 * @param ent Entity to refresh
	 */
	public void refreshEntity(Entity ent) {
		Entity3D ent3d = entities.get(ent);
		if (ent3d == null) {
			return;
		}
		ent3d.refresh();
	}
	
	/**
	 * Refresh the visual element of all the entities in the scene.
	 */
	public void refreshEntities() {
		for (Entity3D ent3d : entities.values()) {
			ent3d.refresh();
		}
	}

}
