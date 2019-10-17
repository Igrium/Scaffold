package org.metaversemedia.scaffold.editor.editor3d;

import java.nio.file.Path;

import org.metaversemedia.scaffold.editor.ui.EditorWindow;
import org.metaversemedia.scaffold.level.entity.Entity;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.scene.Spatial;

/**
 * The 3d rendered part of the editor.
 * @author Sam54123
 */
public class EditorApp extends SimpleApplication {
	
	private EditorWindow parent;
	
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
		org.metaversemedia.scaffold.core.AssetManager scaffoldAssetManager = parent.getProject().assetManager();
		for (Path p : scaffoldAssetManager.getLoadedPaths()) {
			getAssetManager().registerLocator(p.resolve("assets").toString(), FileLocator.class);
		}
		
		getAssetManager().registerLocator("assets", ClasspathLocator.class); // Default assets
		
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
		
//		Box b = new Box(1, 1, 1);
//		Geometry geom = new Geometry("Box", b);
//
//		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//		Texture tex = assetManager.loadTexture("minecraft/textures/block/stone.png");
//		tex.setMagFilter(MagFilter.Nearest);
//		mat.setTexture("ColorMap", tex);
//		geom.setMaterial(mat);
//
//		rootNode.attachChild(geom);
		
		
		
		Entity ent = parent.getLevel().getEntity("testEnt");
		rootNode.attachChild(new Entity3D(ent, this));
	}

}
