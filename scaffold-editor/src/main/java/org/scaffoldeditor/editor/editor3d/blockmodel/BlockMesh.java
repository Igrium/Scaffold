package org.scaffoldeditor.editor.editor3d.blockmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scaffoldeditor.editor.editor3d.blockmodel.ModelElement.Face;
import org.scaffoldeditor.editor.editor3d.blockmodel.ModelElement.CullFace;
import org.scaffoldeditor.editor.editor3d.util.EditorUtils;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * A mesh used to store there rendering data of a block model.
 * @author Sam54123
 */
public class BlockMesh extends Mesh {
	
	
	private class CreateFaceReturn {
		public List<Vector2f> texCoords;
		public List<Integer> indices;
	}
	
	private List<ModelElement> elements;
	
	// Keep track of everything that creates a mesh in different groups so they can be turned off.
	// texCoord and indices are stored with respect to local array.
	private Map<ModelElement.CullFace, List<Vector3f>> vertexGroups;
	private Map<ModelElement.CullFace, List<Vector2f>> texCoordGroups;
	private Map<ModelElement.CullFace, List<Integer>> indexGroups;
	
	public BlockMesh() {};
	
	/**
	 * Create a block mesh from a set of elements.
	 * @param elements Elements to create from;
	 * taken from the elements tag of a <a href=https://minecraft.gamepedia.com/Model#Block_models>block model.</a> 
	 */
	public BlockMesh(JSONArray elements) {
		for (int i = 0; i < elements.length(); i++) {
			loadElement(elements.getJSONObject(i));
		}
		recompileElements();
	}
	
	/**
	 * Get a list of all the elements in the model.
	 * @return Mutable list of all elements.
	 */
	public List<ModelElement> getElements() {
		return elements;
	}
	
	/**
	 * Re-compile all the elements in the block mesh into their sets of vertices.
	 */
	public void recompileElements() {
		clear();
		for (ModelElement e : elements) {
			compileElement(e);
		}
		
		/**
		 * Insure this object can be used by vanilla JME methods.
		 */
		compileMesh(this, true, true, true, true, true, true);
	}
	
	/**
	 * Compile the block mesh into a mesh readable by JME
	 * @param mesh Mesh to compile into. WILL CLEAR BUFFERS
	 * @param upVisible Is the top face visible?
	 * @param downVisible Is the bottom face visible?
	 * @param northVisible Is the north face visible?
	 * @param southVisible Is the south face visible?
	 * @param eastVisible Is the east face visible?
	 * @param westVisible Is the west face visible?
	 */
	public void compileMesh(Mesh mesh, boolean upVisible, boolean downVisible,
			boolean northVisible, boolean southVisible, boolean eastVisible, boolean westVisible) {
		
		List<Vector3f> vertBuffer = new ArrayList<Vector3f>();
		List<Vector2f> texCoordBuffer = new ArrayList<Vector2f>();
		List<Integer> indexBuffer = new ArrayList<Integer>();
		
		if (upVisible) {
			addVertsToBuffers(vertBuffer, texCoordBuffer, indexBuffer, vertexGroups.get(CullFace.UP),  texCoordGroups.get(CullFace.UP), indexGroups.get(CullFace.UP));
		}
		
		if (downVisible) {
			addVertsToBuffers(vertBuffer, texCoordBuffer, indexBuffer, vertexGroups.get(CullFace.DOWN),  texCoordGroups.get(CullFace.DOWN), indexGroups.get(CullFace.DOWN));
		}
		
		if (northVisible) {
			addVertsToBuffers(vertBuffer, texCoordBuffer, indexBuffer, vertexGroups.get(CullFace.NORTH),  texCoordGroups.get(CullFace.NORTH), indexGroups.get(CullFace.NORTH));
		}
		
		if (southVisible) {
			addVertsToBuffers(vertBuffer, texCoordBuffer, indexBuffer, vertexGroups.get(CullFace.SOUTH),  texCoordGroups.get(CullFace.SOUTH), indexGroups.get(CullFace.SOUTH));
		}
		
		if (eastVisible) {
			addVertsToBuffers(vertBuffer, texCoordBuffer, indexBuffer, vertexGroups.get(CullFace.EAST),  texCoordGroups.get(CullFace.EAST), indexGroups.get(CullFace.EAST));
		}
		
		if (westVisible) {
			addVertsToBuffers(vertBuffer, texCoordBuffer, indexBuffer, vertexGroups.get(CullFace.WEST),  texCoordGroups.get(CullFace.WEST), indexGroups.get(CullFace.WEST));
		}
		
		addVertsToBuffers(vertBuffer, texCoordBuffer, indexBuffer, vertexGroups.get(CullFace.NONE),  texCoordGroups.get(CullFace.NONE), indexGroups.get(CullFace.NONE));
		
		mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertBuffer.toArray(new Vector3f[vertBuffer.size()])));
		mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoordBuffer.toArray(new Vector2f[texCoordBuffer.size()])));
		mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(integerListToInt(indexBuffer)));
		mesh.updateBound();
	}
	
	private int[] integerListToInt(List<Integer> list) {
		int[] array = new int[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}
	
	
	/**
	 * Load an element onto the block mesh.
	 * @param element Element to load.
	 */
	protected void loadElement(JSONObject element) {
		elements.add(new ModelElement(element));
	}
	
	/**
	 * Compile a model element onto the model.
	 * @param element Element to load
	 */
	protected void compileElement(ModelElement element) {
		appendFace(element.getUpFace());
		appendFace(element.getDownFace());
		appendFace(element.getNorthFace());
		appendFace(element.getSouthFace());
		appendFace(element.getEastFace());
		appendFace(element.getWestFace());
	}
	
	/**
	 * Clear all the compiled mesh data.
	 * ONLY CALL IF YOU KNOW WHAT YOU'RE DOING
	 */
	protected void clear() {
		vertexGroups.clear();
		texCoordGroups.clear();
		indexGroups.clear();
	}
	
	private void appendFace(Face face) {
		if (face != null) {
			appendVerts(face.vertices, face.texCoords, face.indices, face.cullFace);
		}
	}
	
	/**
	 * Append a set of vertices to the model.
	 * @param vertices
	 * @param texCoord UV coordinates.
	 * @param indices
	 * @param side The side to apply it to.
	 */
	private void appendVerts(List<Vector3f> vertices, List<Vector2f> texCoord, List<Integer> indices, ModelElement.CullFace side) {
		// Make sure mesh maps are initialized.
		if (!vertexGroups.containsKey(side)) {
			vertexGroups.put(side, new ArrayList<Vector3f>());
		}
		if (!texCoordGroups.containsKey(side)) {
			texCoordGroups.put(side, new ArrayList<Vector2f>());
		}
		if (!indexGroups.containsKey(side)) {
			indexGroups.put(side, new ArrayList<Integer>());
		}
		
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
	
	private void addVertsToBuffers(List<Vector3f> vertBuffer, List<Vector2f> texCoordBuffer, List<Integer> indexBuffer,
			List<Vector3f> vertices, List<Vector2f> texCoord, List<Integer> indices) {
		// index offset of buffers.
		int offset = vertBuffer.size();
		
		vertBuffer.addAll(vertices);
		texCoordBuffer.addAll(texCoord);
		
		// Clone indices so we don't screw up the passed list
		List<Integer> correctedIndices = new ArrayList<Integer>(indices);
		for (int i = 0; i < correctedIndices.size(); i++) {
			correctedIndices.set(i, correctedIndices.get(i) + offset);
		}
		
		indexBuffer.addAll(correctedIndices);
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
