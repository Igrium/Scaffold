package org.scaffoldeditor.editor.editor3d.blockmodel;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * A struct keeping all the buffers for a mesh in an easily accessable format.
 * @author Igrium
 */
public class LightMesh {
	public List<Vector3f> vertBuffer = new ArrayList<Vector3f>();
	public List<Vector2f> texCoordBuffer = new ArrayList<Vector2f>();
	public List<Integer> indexBuffer = new ArrayList<Integer>();
	public List<Vector3f> normalBuffer = new ArrayList<Vector3f>();
	
	/**
	 * Create a full JME mesh from this light mesh.
	 * @param mesh Mesh to compile into. WILL CLEAR BUFFERS.
	 * @return The passed mesh.
	 */
	public Mesh toMesh(Mesh mesh) {
		mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertBuffer.toArray(new Vector3f[vertBuffer.size()])));
		mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoordBuffer.toArray(new Vector2f[texCoordBuffer.size()])));
		mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(integerListToInt(indexBuffer)));
		mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normalBuffer.toArray(new Vector3f[normalBuffer.size()])));
		mesh.updateBound();
		
		BlockMeshUtils.generateTangent(mesh);
		
		return mesh;
	}
	
	private int[] integerListToInt(List<Integer> list) {
		int[] array = new int[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}
}