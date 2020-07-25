package org.scaffoldeditor.editor.editor3d.blockmodel;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.rvandoosselaer.blocks.ChunkMesh;
import com.rvandoosselaer.blocks.Shape;
import com.simsilica.mathd.Vec3i;

/**
 * Utility class for assisting with block meshes.
 * @author Igrium
 */
public class BlockMeshUtils {
	
	/**
	 * Add a mesh to the voxel world.
	 * @param mesh Light mesh to add.
	 * @param location Block location to add in.
	 * @param chunkMesh Chunk mesh to add to.
	 * @param blockScale Block scale
	 */
	public static void addMesh(LightMesh mesh, Vec3i location, ChunkMesh chunkMesh, float blockScale) {
		int offset = chunkMesh.getPositions().size();
		
		for (Vector3f vertex : mesh.vertBuffer) {
			chunkMesh.getPositions().add(Shape.createVertex(vertex, location, blockScale));
		}
		
		for (Vector2f uv : mesh.texCoordBuffer) {
			chunkMesh.getUvs().add(uv);
		}
		
		for (int index : mesh.indexBuffer) {
			chunkMesh.getIndices().add(index + offset);
		}
	}
}
