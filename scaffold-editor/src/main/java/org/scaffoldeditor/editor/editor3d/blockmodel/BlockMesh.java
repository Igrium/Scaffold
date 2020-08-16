package org.scaffoldeditor.editor.editor3d.blockmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scaffoldeditor.editor.editor3d.blockmodel.ModelElement.Face;
import org.scaffoldeditor.editor.editor3d.blockmodel.ModelElement.CullFace;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.rvandoosselaer.blocks.Chunk;
import com.rvandoosselaer.blocks.ChunkMesh;
import com.rvandoosselaer.blocks.Direction;
import com.rvandoosselaer.blocks.Shape;
import com.simsilica.mathd.Vec3i;

/**
 * A mesh used to store there rendering data of a block model.
 * @author Igrium
 */
public class BlockMesh extends Mesh implements Shape {
	
	/**
	 * A value from 0-3 indicating how many times this model has been rotated 90 degrees
	 */
	private int modelRotation = 0;
	
	private List<ModelElement> elements = new ArrayList<ModelElement>();
	
	// Keep track of everything that creates a mesh in different groups so they can be turned off.
	// texCoord and indices are stored with respect to local array.
	private Map<ModelElement.CullFace, List<Vector3f>> vertexGroups = new HashMap<ModelElement.CullFace, List<Vector3f>>();
	private Map<ModelElement.CullFace, List<Vector2f>> texCoordGroups = new HashMap<ModelElement.CullFace, List<Vector2f>>();
	private Map<ModelElement.CullFace, List<Integer>> indexGroups = new HashMap<ModelElement.CullFace, List<Integer>>();
	private Map<ModelElement.CullFace, List<Vector3f>> normalGroups = new HashMap<ModelElement.CullFace, List<Vector3f>>();
	
	public BlockMesh() {};
	
	/**
	 * Create a block mesh from a set of elements.
	 * @param elements Elements to create from;
	 * taken from the elements tag of a <a href=https://minecraft.gamepedia.com/Model#Block_models>block model.</a> 
	 */
	public BlockMesh(JSONArray elements) {
		this(elements, 0);
	}
	
	/**
	 * Create a block mesh from a set of elements.
	 * @param elements Elements to create from;
	 * taken from the elements tag of a <a href=https://minecraft.gamepedia.com/Model#Block_models>block model.</a>
	 * @param modelRotation The rotation to be applied to the model in degrees.
	 * Will be rounded down to multiple of 90.
	 */
	public BlockMesh(JSONArray elements, int modelRotation) {
		setModelRotation(modelRotation);
		for (int i = 0; i < elements.length(); i++) {
			loadElement(elements.getJSONObject(i));
		}
		recompileElements();
	}
	
	public BlockMesh(List<ModelElement> elements, int modelRotation) {
		setModelRotation(modelRotation);
		for (ModelElement element : elements) {
			this.elements.add(element.getRotatedCopy(modelRotation));
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
	 * Get the rotation of the model.
	 * @return Model rotation in degrees.
	 */
	public int getModelRotation() {
		return modelRotation * 90;
	}
	
	/**
	 * Set the model rotation.
	 * ELEMENDS MUST BE RE-CREATED AFTER SET
	 * @param value Model rotation in degrees. Rounded down to the nearest 90.
	 */
	protected void setModelRotation(int value) {
		int temp = Math.floorDiv(value, 90);
		
		// insure value is between 0 and 3.
		int translation = Math.floorDiv(value, 4);
		modelRotation = temp - translation;
	}
	
	public BlockMesh getRotatedCopy(int rotation) {
		return new BlockMesh(elements, rotation);
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
		
		LightMesh lMesh = compileMesh(upVisible, downVisible, northVisible, southVisible, eastVisible, westVisible);
		lMesh.toMesh(mesh);	
	}
	
	/**
	 * Compile the block mesh into a light mesh.
	 * @param upVisible Is the top face visible?
	 * @param downVisible Is the bottom face visible?
	 * @param northVisible Is the north face visible?
	 * @param southVisible Is the south face visible?
	 * @param eastVisible Is the east face visible?
	 * @param westVisible Is the west face visible?
	 * @return Compiled light mesh.
	 */
	public LightMesh compileMesh(boolean upVisible, boolean downVisible,
			boolean northVisible, boolean southVisible, boolean eastVisible, boolean westVisible) {
		LightMesh lMesh = new LightMesh();
		

		if (upVisible && vertexGroups.containsKey(CullFace.UP)) {
			addVertsToBuffers(lMesh.vertBuffer, lMesh.texCoordBuffer, lMesh.indexBuffer, lMesh.normalBuffer,
					vertexGroups.get(CullFace.UP),  texCoordGroups.get(CullFace.UP), indexGroups.get(CullFace.UP), normalGroups.get(CullFace.UP));
		}
		
		if (downVisible && vertexGroups.containsKey(CullFace.DOWN)) {
			addVertsToBuffers(lMesh.vertBuffer, lMesh.texCoordBuffer, lMesh.indexBuffer, lMesh.normalBuffer,
					vertexGroups.get(CullFace.DOWN),  texCoordGroups.get(CullFace.DOWN), indexGroups.get(CullFace.DOWN), normalGroups.get(CullFace.DOWN));
		}
		
		if (northVisible && vertexGroups.containsKey(CullFace.NORTH)) {
			addVertsToBuffers(lMesh.vertBuffer, lMesh.texCoordBuffer, lMesh.indexBuffer, lMesh.normalBuffer,
					vertexGroups.get(CullFace.NORTH),  texCoordGroups.get(CullFace.NORTH), indexGroups.get(CullFace.NORTH), normalGroups.get(CullFace.NORTH));
		}
		
		if (southVisible && vertexGroups.containsKey(CullFace.SOUTH)) {
			addVertsToBuffers(lMesh.vertBuffer, lMesh.texCoordBuffer, lMesh.indexBuffer, lMesh.normalBuffer,
					vertexGroups.get(CullFace.SOUTH),  texCoordGroups.get(CullFace.SOUTH), indexGroups.get(CullFace.SOUTH), normalGroups.get(CullFace.SOUTH));
		}
		
		if (eastVisible && vertexGroups.containsKey(CullFace.EAST)) {
			addVertsToBuffers(lMesh.vertBuffer, lMesh.texCoordBuffer, lMesh.indexBuffer, lMesh.normalBuffer,
					vertexGroups.get(CullFace.EAST),  texCoordGroups.get(CullFace.EAST), indexGroups.get(CullFace.EAST), normalGroups.get(CullFace.EAST));
		}
		
		if (westVisible && vertexGroups.containsKey(CullFace.WEST)) {
			addVertsToBuffers(lMesh.vertBuffer, lMesh.texCoordBuffer, lMesh.indexBuffer, lMesh.normalBuffer,
					vertexGroups.get(CullFace.WEST),  texCoordGroups.get(CullFace.WEST), indexGroups.get(CullFace.WEST), normalGroups.get(CullFace.WEST));
		}
		
		if (vertexGroups.containsKey(CullFace.NONE)) {
			addVertsToBuffers(lMesh.vertBuffer, lMesh.texCoordBuffer, lMesh.indexBuffer, lMesh.normalBuffer,
					vertexGroups.get(CullFace.NONE),  texCoordGroups.get(CullFace.NONE), indexGroups.get(CullFace.NONE), normalGroups.get(CullFace.NONE));
		}
		
		return lMesh;
	}
	
	@Override
	public void add(Vec3i location, Chunk chunk, ChunkMesh chunkMesh) {
		boolean upVisible = chunk.isFaceVisible(location, Direction.TOP);
		boolean downVisible = chunk.isFaceVisible(location, Direction.BOTTOM);
		boolean northVisible = chunk.isFaceVisible(location, Direction.FRONT);
		boolean southVisible = chunk.isFaceVisible(location, Direction.BACK);
		boolean eastVisible = chunk.isFaceVisible(location, Direction.LEFT);
		boolean westVisible = chunk.isFaceVisible(location, Direction.RIGHT); // East and west may have to be flipped.
		
		Mesh mesh = compileMesh(upVisible, downVisible, northVisible, southVisible, eastVisible, westVisible).toMesh(new Mesh());
		BlockMeshUtils.fillFromMesh(mesh, location, chunkMesh, 1.0f);
		
	}
	
	
	/**
	 * Load an element onto the block mesh.
	 * @param element Element to load.
	 */
	protected void loadElement(JSONObject element) {
		elements.add(new ModelElement(element, getModelRotation()));
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
	 * Get the new direction (north, south, east, west) of a face, taking model rotation into account.
	 * @param face Original face.
	 * @return Rotated face.
	 */
	protected CullFace getRotatedFace(CullFace face) {
		if (modelRotation == 0 || face == CullFace.UP || face == CullFace.DOWN || face == CullFace.NONE) {
			return face;
		}
		
		// TODO: Can you do this with pure math? Given the nature of enums, all I can think to do is brute force it.
		if (face == CullFace.NORTH && modelRotation == 0) { return CullFace.NORTH; }
		if (face == CullFace.NORTH && modelRotation == 1) { return CullFace.WEST; }
		if (face == CullFace.NORTH && modelRotation == 2) { return CullFace.SOUTH; }
		if (face == CullFace.NORTH && modelRotation == 3) { return CullFace.EAST; }
		
		if (face == CullFace.WEST && modelRotation == 0) { return CullFace.WEST; }
		if (face == CullFace.WEST && modelRotation == 1) { return CullFace.SOUTH; }
		if (face == CullFace.WEST && modelRotation == 2) { return CullFace.EAST; }
		if (face == CullFace.WEST && modelRotation == 3) { return CullFace.NORTH; }
		
		if (face == CullFace.SOUTH && modelRotation == 0) { return CullFace.SOUTH; }
		if (face == CullFace.SOUTH && modelRotation == 1) { return CullFace.EAST; }
		if (face == CullFace.SOUTH && modelRotation == 3) { return CullFace.NORTH; }
		if (face == CullFace.SOUTH && modelRotation == 3) { return CullFace.WEST; }
		
		if (face == CullFace.EAST && modelRotation == 0) { return CullFace.EAST; }
		if (face == CullFace.EAST && modelRotation == 1) { return CullFace.NORTH; }
		if (face == CullFace.EAST && modelRotation == 2) { return CullFace.WEST; }
		if (face == CullFace.EAST && modelRotation == 3) { return CullFace.SOUTH; }
		
		
		return face;
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
			appendVerts(face.vertices, face.texCoords, face.indices, face.normals, getRotatedFace(face.cullFace));
		}
	}
	
	/**
	 * Append a set of vertices to the model.
	 * @param vertices
	 * @param texCoord UV coordinates.
	 * @param indices
	 * @param side The side to apply it to.
	 */
	private void appendVerts(List<Vector3f> vertices, List<Vector2f> texCoord, List<Integer> indices, List<Vector3f> normals, ModelElement.CullFace side) {
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
		if (!normalGroups.containsKey(side)) {
			normalGroups.put(side, new ArrayList<Vector3f>());
		}
		
		List<Vector3f> meshVerts = vertexGroups.get(side);
		List<Vector2f> meshTexCoord = texCoordGroups.get(side);
		List<Integer> meshIndices = indexGroups.get(side);
		List<Vector3f> meshNormals = normalGroups.get(side);
		
		// The index offset in the mesh.
		int offset = meshVerts.size();
		
		meshVerts.addAll(vertices);
		meshTexCoord.addAll(texCoord);
		meshNormals.addAll(normals);
		
		// Clone indices so we don't screw up the passed array.
		List<Integer> correctedIndices = new ArrayList<Integer>(indices);
		for (int i = 0; i < correctedIndices.size(); i++) {
			correctedIndices.set(i, correctedIndices.get(i) + offset);
		}
		
		meshIndices.addAll(correctedIndices);
	}
	
	private void addVertsToBuffers(List<Vector3f> vertBuffer, List<Vector2f> texCoordBuffer, List<Integer> indexBuffer, List<Vector3f> normalBuffer,
			List<Vector3f> vertices, List<Vector2f> texCoord, List<Integer> indices, List<Vector3f> normals) {
		// index offset of buffers.
		int offset = vertBuffer.size();
		
		vertBuffer.addAll(vertices);
		texCoordBuffer.addAll(texCoord);
		normalBuffer.addAll(normals);
		
		// Clone indices so we don't screw up the passed list
		List<Integer> correctedIndices = new ArrayList<Integer>(indices);
		for (int i = 0; i < correctedIndices.size(); i++) {
			correctedIndices.set(i, correctedIndices.get(i) + offset);
		}
		
		indexBuffer.addAll(correctedIndices);
	}
	
}
