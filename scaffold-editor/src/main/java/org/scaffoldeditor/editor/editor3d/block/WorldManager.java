package org.scaffoldeditor.editor.editor3d.block;

import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.Chunk;

import com.jme3.scene.Node;
import com.rvandoosselaer.blocks.BlocksConfig;
import com.rvandoosselaer.blocks.ChunkMeshGenerator;
import com.simsilica.mathd.Vec3i;

public final class WorldManager {
	
	/**
	 * Load a block world into the engine.
	 * @param world World to load.
	 * @param rootNode Node to add to.
	 */
	public static void loadWorld(BlockWorld world, Node rootNode) {
		System.out.println("Loading render world...");
		ChunkRegistry.clean();
		
		for (ChunkCoordinate coord : world.chunkCoords()) {
			Chunk chunk = world.chunkAt(coord);
			
			com.rvandoosselaer.blocks.Chunk rChunk = ChunkRegistry.registerChunk(chunk, new Vec3i(coord.x(), 0, coord.z()));
			ChunkRegistry.refreshChunk(chunk);
			
			ChunkMeshGenerator meshGenerator = BlocksConfig.getInstance().getChunkMeshGenerator();
			rChunk.createNode(meshGenerator);
			
			rootNode.attachChild(rChunk.getNode());
		}
	}
	
	/**
	 * Refresh a chunk in the world.
	 * @param chunk Chunk to refresh.
	 * @param rootNode World to refresh in.
	 */
	public static void refreshChunk(Chunk chunk, Node rootNode) {
		ChunkRegistry.refreshChunk(chunk);
		
		ChunkMeshGenerator meshGenerator = BlocksConfig.getInstance().getChunkMeshGenerator();
		com.rvandoosselaer.blocks.Chunk rChunk = ChunkRegistry.get(chunk);
		rChunk.getNode().removeFromParent();
		rChunk.createNode(meshGenerator);
		rootNode.attachChild(rChunk.getNode());
	}

}
