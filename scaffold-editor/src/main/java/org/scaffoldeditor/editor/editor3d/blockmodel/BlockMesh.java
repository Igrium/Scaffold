package org.scaffoldeditor.editor.editor3d.blockmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scaffoldeditor.editor.editor3d.util.EditorUtils;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

/**
 * A mesh used to store there rendering data of a block model.
 * @author Sam54123
 */
public class BlockMesh extends Mesh {
	
	/**
	 * Represents different sides of a block mesh.
	 */
	public enum MeshFaceSide {
			UP, DOWN, NORTH, SOUTH, EAST, WEST, MASTER
	}
	
	private class CreateFaceReturn {
		public List<Vector2f> texCoords;
		public List<Integer> indices;
	}
	
	// Keep track of everything that creates a mesh in different groups so they can be turned off.
	// texCoord and indices are stored with respect to local array.
	private Map<MeshFaceSide, List<Vector3f>> vertexGroups;
	private Map<MeshFaceSide, List<Vector2f>> texCoordGroups;
	private Map<MeshFaceSide, List<Integer>> indexGroups;
	
	public BlockMesh() {};
	
	/**
	 * Create a block mesh from a set of elements.
	 * @param elements Elements to create from;
	 * taken from the elements tag of a <a href=https://minecraft.gamepedia.com/Model#Block_models>block model.</a> 
	 */
	public BlockMesh(JSONArray elements) {
		
	}
	
	/**
	 * Load an element onto the block mesh.
	 * @param element Element to load.
	 */
	protected void loadElement(JSONObject element) {
		Vector3f from = EditorUtils.jsonArrayToMVector(element.getJSONArray("from"));
		Vector3f to = EditorUtils.jsonArrayToMVector(element.getJSONArray("to"));
		
		JSONObject faces = element.getJSONObject("faces");
		
		// TODO: SUPPORT FACE TEXTURE ROTATION
	}
	
	/**
	 * Finish the process of creating a face from a list of vertices.
	 * @param vertices The input vertices going counterclockwise.
	 * @param texCoords The texture coordinates to apply to the plane. Scheme: [x1, y1, x2, y2]
	 * @return The generated values with respect to the input array.
	 */
	private CreateFaceReturn createFace(List<Vector3f> vertices, float[] texCoords) {
		CreateFaceReturn ret = new CreateFaceReturn();
		ret.texCoords = new ArrayList<Vector2f>();
		
		// TODO: Make sure UVs are being added correctly.
		if (texCoords != null) {
			ret.texCoords.add(new Vector2f(texCoords[0], texCoords[1]));
			ret.texCoords.add(new Vector2f(texCoords[2], texCoords[1]));
			ret.texCoords.add(new Vector2f(texCoords[2], texCoords[3]));
			ret.texCoords.add(new Vector2f(texCoords[0], texCoords[3]));
		} else {
			ret.texCoords.add(new Vector2f(0, 0));
			ret.texCoords.add(new Vector2f(1, 0));
			ret.texCoords.add(new Vector2f(1, 1));
			ret.texCoords.add(new Vector2f(0, 1));
		}
		
		
		ret.indices = new ArrayList<Integer>();
		
		ret.indices.addAll(Arrays.asList(new Integer[] {0,1,3}));
		ret.indices.addAll(Arrays.asList(new Integer[] {3,1,2}));
		
		return ret;
	}
	
	/**
	 * Create the top face of an element.
	 * @param face 
	 * @param from
	 * @param to
	 */
	private void createUpFace(JSONObject face, Vector3f from, Vector3f to) {
		List<Vector3f> vertices = vertexGroups.get(MeshFaceSide.UP);
		int offset = vertices.size();
		
		// Create vertices of the face.
		List<Vector3f> localVerts = Arrays.asList(new Vector3f[] {
				new Vector3f(from.x, to.y, from.z),
				new Vector3f(to.x, to.y, from.z),
				to,
				new Vector3f(from.x, to.y, to.z)
		});
		
		CreateFaceReturn genFace = createFace(localVerts, jsonToFloatArray(face.optJSONArray("uv")));
		
		appendVerts(localVerts, genFace.texCoords, genFace.indices, MeshFaceSide.UP);
	}
	
	/**
	 * Append a set of faces to the model.
	 * @param vertices
	 * @param texCoord UV coordinates.
	 * @param indices
	 * @param side The side to apply it to.
	 */
	private void appendVerts(List<Vector3f> vertices, List<Vector2f> texCoord, List<Integer> indices, MeshFaceSide side) {
		List<Vector3f> meshVerts = vertexGroups.get(side);
		List<Vector2f> meshTexCoord = texCoordGroups.get(side);
		List<Integer> meshIndices = indexGroups.get(side);
		
		// The index offset in the mesh.
		int offset = meshVerts.size();
		
		meshVerts.addAll(vertices);
		meshTexCoord.addAll(texCoord);
		
		// Clone indices so we don't screw up the passed array.
		List<Integer> correctedIndices = new ArrayList<Integer>(indices);
		for (int i = 0; i < correctedIndices.size(); i++) {
			correctedIndices.set(i, correctedIndices.get(i) + offset);
		}
		
		meshIndices.addAll(correctedIndices);
	}
	
	private static float[] jsonToFloatArray(JSONArray in) {
		if (in == null) {
			return null;
		}
		
		float[] array = new float[in.length()];
		
		for (int i = 0; i < in.length(); i++) {
			array[i] = in.getFloat(i);
		}
		return array;
	}
	
}
