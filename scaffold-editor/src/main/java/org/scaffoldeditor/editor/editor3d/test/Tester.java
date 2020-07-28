package org.scaffoldeditor.editor.editor3d.test;

import org.scaffoldeditor.editor.editor3d.EditorApp;
import org.scaffoldeditor.scaffold.core.Project;
import com.jme3.scene.Spatial;
import com.rvandoosselaer.blocks.Block;
import com.rvandoosselaer.blocks.BlockIds;
import com.rvandoosselaer.blocks.BlocksConfig;
import com.rvandoosselaer.blocks.Chunk;
import com.rvandoosselaer.blocks.ChunkMeshGenerator;
import com.rvandoosselaer.blocks.ShapeIds;
import com.rvandoosselaer.blocks.TypeIds;
import com.simsilica.mathd.Vec3i;

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
		
		Chunk chunk = new Chunk(new Vec3i(0,0,0));
		
		Block block1 = app.getBlockManager().get("minecraft/models/block/cobblestone_stairs.json");
		Block block2 = app.getBlockManager().get("minecraft/models/block/flower_pot.json");
//		Block block3 = app.getBlockManager().get("saudghauisd");
//		Block block4 = app.getBlockManager().get(ShapeIds.CUBE);
		
		chunk.addBlock(0, 0, 2, block1);
		chunk.addBlock(0, 0, 3, block2);
//		chunk.addBlock(0, 0, 3, block3);
//		chunk.addBlock(0, 0, 4, block4);
		
		ChunkMeshGenerator meshGenerator = BlocksConfig.getInstance().getChunkMeshGenerator();
		chunk.createNode(meshGenerator);
		
		app.getRootNode().attachChild(chunk.getNode());
				
//		Spatial model = app.getAssetManager().loadModel("minecraft/models/block/cobblestone_stairs.json");
//		Spatial model2 = app.getAssetManager().loadModel("minecraft/models/block/birch_stairs.json");
//		
//		model2.setLocalTranslation(0, 0, 1);
//		
//		app.getRootNode().attachChild(model);
//		app.getRootNode().attachChild(model2);
		

	}
}
