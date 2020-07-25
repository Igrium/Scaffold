package org.scaffoldeditor.editor.editor3d.test;

import org.scaffoldeditor.editor.editor3d.EditorApp;
import org.scaffoldeditor.scaffold.core.Project;
import com.jme3.scene.Spatial;

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

		
		Spatial model = app.getAssetManager().loadModel("models/testModel.json");
		
		app.getRootNode().attachChild(model);
	}
}
