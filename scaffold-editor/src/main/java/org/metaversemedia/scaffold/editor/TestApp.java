package org.metaversemedia.scaffold.editor;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class TestApp extends SimpleApplication {

	public static void main(String[] args) {
		TestApp app = new TestApp();
		app.start();
	}
	
	@Override
	public void simpleInitApp() {
		Box b = new Box(1,1,1);
		Geometry geom = new Geometry("Box", b);
		
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		
		rootNode.attachChild(geom);
	}

}
