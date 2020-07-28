package org.scaffoldeditor.editor.editor3d.blockmodel;

import org.json.JSONArray;

import com.rvandoosselaer.blocks.Chunk;
import com.rvandoosselaer.blocks.ChunkMesh;
import com.simsilica.mathd.Vec3i;

/**
 * A block mesh with no vertices. Used when the model of a block can't be found.
 * @author Igrium
 */
public class EmptyBlockMesh extends BlockMesh {
	
	public EmptyBlockMesh() {
		super(new JSONArray());
	}
	
	@Override
	public LightMesh compileMesh(boolean upVisible, boolean downVisible, boolean northVisible, boolean southVisible,
			boolean eastVisible, boolean westVisible) {
		return new LightMesh();
	}
	
	@Override
	public void add(Vec3i location, Chunk chunk, ChunkMesh chunkMesh) {
		return;
	}
	
	@Override
	public void recompileElements() {
		return;
	}
}
