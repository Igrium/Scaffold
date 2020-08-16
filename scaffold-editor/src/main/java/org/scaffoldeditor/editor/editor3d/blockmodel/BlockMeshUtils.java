package org.scaffoldeditor.editor.editor3d.blockmodel;

import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.TangentUtils;
import com.jme3.util.mikktspace.MikkTSpaceImpl;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;
import com.rvandoosselaer.blocks.ChunkMesh;
import com.rvandoosselaer.blocks.Shape;
import com.simsilica.mathd.Vec3i;

/**
 * Utility class for assisting with block meshes.
 * @author Igrium
 */
public class BlockMeshUtils {
	
	public static final Vector3f MODELCENTER = new Vector3f(8,8,8);
	
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
	
		
	/**
	 * Add a JME mesh to a voxel world.
	 * @param mesh JME mesh to add.
	 * @param location Block location to add in.
	 * @param chunkMesh Chunk mesh to add to.
	 * @param blockScale Block scale.
	 * @author Ali_RS
	 */
	public static void fillFromMesh(Mesh mesh, Vec3i location, ChunkMesh chunkMesh, float blockScale) {
	    // calculate index offset, we setUsing this to connect the triangles
	    int offset = chunkMesh.getPositions().size();

	    // vertices
	    int numVertices = mesh.getVertexCount();
	    for (int vertexI = 0; vertexI < numVertices; ++vertexI) {
	        chunkMesh.getPositions().add(Shape.createVertex(vertexVector3f(mesh, VertexBuffer.Type.Position, vertexI), location, blockScale));
	    }

	    // indices
	    IndexBuffer indexBuffer = mesh.getIndexBuffer();
	    for (int ibPos = 0; ibPos < indexBuffer.size(); ++ibPos) {
	        int index = indexBuffer.get(ibPos);
	        chunkMesh.getIndices().add(index + offset);
	    }

	    if (!chunkMesh.isCollisionMesh()) {
	        // normals, tangents and uvs
	        for (int vertexI = 0; vertexI < numVertices; ++vertexI) {
	            chunkMesh.getNormals().add(vertexVector3f(mesh, VertexBuffer.Type.Normal, vertexI));
	            chunkMesh.getTangents().add(vertexVector4f(mesh, VertexBuffer.Type.Tangent, vertexI));
	            chunkMesh.getUvs().add(vertexVector2f(mesh, VertexBuffer.Type.TexCoord, vertexI));
	        }
	    }
	}

	private static Vector2f vertexVector2f(Mesh mesh, VertexBuffer.Type bufferType, int vertexIndex) {
	    Vector2f vertex = new Vector2f();

	    FloatBuffer floatBuffer = mesh.getFloatBuffer(bufferType);
	    int floatIndex = 2 * vertexIndex;
	    vertex.x = floatBuffer.get(floatIndex);
	    vertex.y = floatBuffer.get(floatIndex + 1);
	    return vertex;
	}

	private static Vector3f vertexVector3f(Mesh mesh, VertexBuffer.Type bufferType, int vertexIndex) {
	    Vector3f vertex = new Vector3f();

	    FloatBuffer floatBuffer = mesh.getFloatBuffer(bufferType);
	    int floatIndex = 3 * vertexIndex;
	    vertex.x = floatBuffer.get(floatIndex);
	    vertex.y = floatBuffer.get(floatIndex + 1);
	    vertex.z = floatBuffer.get(floatIndex + 2);
	    return vertex;
	}
	
	private static Vector4f vertexVector4f(Mesh mesh, VertexBuffer.Type bufferType, int vertexIndex) {
	    Vector4f vertex = new Vector4f();

	    FloatBuffer floatBuffer = mesh.getFloatBuffer(bufferType);
	    int floatIndex = 4 * vertexIndex;
	    vertex.x = floatBuffer.get(floatIndex);
	    vertex.y = floatBuffer.get(floatIndex + 1);
	    vertex.z = floatBuffer.get(floatIndex + 2);
	    vertex.w = floatBuffer.get(floatIndex + 3);
	    return vertex;
	}
	
	/**
	 * Modification of MikktspaceTangentGenerator.generate() that works directly on meshes.
	 * @param mesh Mesh to generate tangents of.
	 */
	public static void generateTangent(Mesh mesh){
		MikkTSpaceImpl context = new MikkTSpaceImpl(mesh);
		if (!MikktspaceTangentGenerator.genTangSpaceDefault(context)) {
			Logger.getLogger(MikktspaceTangentGenerator.class.getName()).log(Level.SEVERE,
					"Failed to generate tangents for mesh " + mesh.toString());
		}
		TangentUtils.generateBindPoseTangentsIfNecessary(mesh);

    }

}
