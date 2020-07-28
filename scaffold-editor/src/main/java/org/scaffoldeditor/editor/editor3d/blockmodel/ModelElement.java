package org.scaffoldeditor.editor.editor3d.blockmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Represents a single model element.
 * @author Igrium
 * @see <a href=https://minecraft.gamepedia.com/Model#Block_models>Model Format</a>
 */
public class ModelElement {
	
	public enum RotationAxis {
		X, Y, Z
	}
	
	public enum CullFace {
		UP, DOWN, NORTH, SOUTH, EAST, WEST, NONE
	}
	
	/**
	 * Represents the rotation of a model element.
	 * @author Igrium
	 */
	public static class ElementRotation {
		
		/**
		 * The origin of the rotation
		 */
		public Vector3f origin = new Vector3f(0,0,0);
		
		/**
		 * Specifies the direction of rotation
		 */
		public RotationAxis axis = RotationAxis.X;
		
		/**
		 * Specifies the angle of rotation. Can be 45 through -45 degrees in 22.5 degree increments.
		 */
		public float angle = 0.0f;
		
		/**
		 * Specifies whether or not to scale the faces across the whole block. Can be true or false. Defaults to false.
		 */
		public boolean rescale = false;
	}
	
	/**
	 * Represents a face as a raw set of indexed points.
	 * @author Igrium
	 */
	public static class Face {
		public List<Vector3f> vertices;
		public List<Integer> indices;
		public List<Vector2f> texCoords;
		public List<Vector3f> normals;
		public CullFace cullFace = CullFace.NONE;
	}
	
	protected ElementRotation rotation;
	protected JSONObject faces;
	
	/**
	 * The start point of the cube.
	 */
	protected Vector3f from;
	
	/**
	 * The end point of the cube.
	 */
	protected Vector3f to;
	
	/**
	 * The spatial that assists the element rotation.
	 */
	private Spatial rotatedSpatial = new Node();
	
	/**
	 * Create a model element from a JSON object.
	 * @param element Element JSON object.
	 */
	public ModelElement(JSONObject element) {
		
		// Parse rotation
		rotation = new ElementRotation();
		JSONObject rotationTag = element.optJSONObject("rotation");
		if (rotationTag != null) {
			rotation.origin = jsonArray3ToVector3(rotationTag.getJSONArray("origin")).divide(16);
			rotation.angle = rotationTag.getFloat("angle");
						
			rotation.rescale = rotationTag.optBoolean("rescale");
			
			String axisString = rotationTag.getString("axis");
			if (axisString.matches("x")) {
				rotation.axis = RotationAxis.X;	
			} else if (axisString.matches("y")) {
				rotation.axis = RotationAxis.Y;
			} else if (axisString.matches("z")) {
				rotation.axis = RotationAxis.Z;
			}
		}
		
		
		// Parse from and to
		from = jsonArray3ToVector3(element.getJSONArray("from")).divide(16);
		to = jsonArray3ToVector3(element.getJSONArray("to")).divide(16);
		
		faces = element.getJSONObject("faces");
		
		setupRotation();
	}
	
	/**
	 * Get the start point of the cube.
	 * @return From value.
	 */
	public Vector3f getFrom() {
		return from;
	}
	
	/**
	 * Get the to value of the cube.
	 * @return To value.
	 */
	public Vector3f getTo() {
		return to;
	}
	
	/**
	 * Get the up face of this element.
	 * @return The up face. NULL IF SHOULD NOT BE RENDERED
	 */
	public Face getUpFace() {
		JSONObject face = faces.optJSONObject("up");
		if (face == null) {
			return null;
		}
		
		List<Vector3f> verts = Arrays.asList(new Vector3f[] {
				getGlobalPosition(new Vector3f(from.x, to.y, from.z)),
				getGlobalPosition(new Vector3f(from.x, to.y, to.z)),
				getGlobalPosition(new Vector3f(to.x, to.y, to.z)),
				getGlobalPosition(new Vector3f(to.x, to.y, from.z))
		});
		
		List<Vector3f> normals = Arrays.asList(new Vector3f[] {
				getGlobalRotation(new Vector3f(0, 1, 0)),
				getGlobalRotation(new Vector3f(0, 1, 0)),
				getGlobalRotation(new Vector3f(0, 1, 0)),
				getGlobalRotation(new Vector3f(0, 1, 0)),
		});
		
		return createFace(verts, normals, face);
	}
	
	/**
	 * Get the down face of this element.
	 * @return The down face. NULL IF SHOULD NOT BE RENDERED
	 */
	public Face getDownFace() {
		JSONObject face = faces.optJSONObject("down");
		if (face == null) {
			return null;
		}
		
		List<Vector3f> verts = Arrays.asList(new Vector3f[] {
				getGlobalPosition(new Vector3f(from.x, from.y, to.z)),
				getGlobalPosition(new Vector3f(from.x, from.y, from.z)),
				getGlobalPosition(new Vector3f(to.x, from.y, from.z)),
				getGlobalPosition(new Vector3f(to.x, from.y, to.z))
		});
		
		List<Vector3f> normals = Arrays.asList(new Vector3f[] {
				getGlobalRotation(new Vector3f(0, -1, 0)),
				getGlobalRotation(new Vector3f(0, -1, 0)),
				getGlobalRotation(new Vector3f(0, -1, 0)),
				getGlobalRotation(new Vector3f(0, -1, 0)),
		});
		
		return createFace(verts, normals, face);
	}
	
	/**
	 * Get the north face of this element.
	 * @return The north face. NULL IF SHOULD NOT BE RENDERED
	 */
	public Face getNorthFace() {
		JSONObject face = faces.optJSONObject("north");
		if (face == null) {
			return null;
		}
		
		List<Vector3f> verts = Arrays.asList(new Vector3f[] {
				getGlobalPosition(new Vector3f(to.x, to.y, from.z)),
				getGlobalPosition(new Vector3f(to.x, from.y, from.z)),
				getGlobalPosition(new Vector3f(from.x, from.y, from.z)),
				getGlobalPosition(new Vector3f(from.x, to.y, from.z))
		});
		
		List<Vector3f> normals = Arrays.asList(new Vector3f[] {
				getGlobalRotation(new Vector3f(0, 0, -1)),
				getGlobalRotation(new Vector3f(0, 0, -1)),
				getGlobalRotation(new Vector3f(0, 0, -1)),
				getGlobalRotation(new Vector3f(0, 0, -1)),
		});
		
		return createFace(verts, normals, face);
	}
	
	/**
	 * Get the south face of this element.
	 * @return The south face. NULL IF SHOULD NOT BE RENDERED
	 */
	public Face getSouthFace() {
		JSONObject face = faces.optJSONObject("south");
		if (face == null) {
			return null;
		}
		
		List<Vector3f> verts = Arrays.asList(new Vector3f[] {
				getGlobalPosition(new Vector3f(from.x, to.y, to.z)),
				getGlobalPosition(new Vector3f(from.x, from.y, to.z)),
				getGlobalPosition(new Vector3f(to.x, from.y, to.z)),
				getGlobalPosition(new Vector3f(to.x, to.y, to.z))
		});
		
		List<Vector3f> normals = Arrays.asList(new Vector3f[] {
				getGlobalRotation(new Vector3f(0, 0, 1)),
				getGlobalRotation(new Vector3f(0, 0, 1)),
				getGlobalRotation(new Vector3f(0, 0, 1)),
				getGlobalRotation(new Vector3f(0, 0, 1)),
		});
		
		return createFace(verts, normals, face);
	}
	
	/**
	 * Get the west face of this element.
	 * @return The west face. NULL IF SHOULD NOT BE RENDERED
	 */
	public Face getWestFace() {
		JSONObject face = faces.optJSONObject("west");
		if (face == null) {
			return null;
		}
		
		List<Vector3f> verts = Arrays.asList(new Vector3f[] {
				getGlobalPosition(new Vector3f(from.x, to.y, from.z)),
				getGlobalPosition(new Vector3f(from.x, from.y, from.z)),
				getGlobalPosition(new Vector3f(from.x, from.y, to.z)),
				getGlobalPosition(new Vector3f(from.x, to.y, to.z))
		});
		
		List<Vector3f> normals = Arrays.asList(new Vector3f[] {
				getGlobalRotation(new Vector3f(-1, 0, 0)),
				getGlobalRotation(new Vector3f(-1, 0, 0)),
				getGlobalRotation(new Vector3f(-1, 0, 0)),
				getGlobalRotation(new Vector3f(-1, 0, 0)),
		});
		
		return createFace(verts, normals, face);
	}
	
	/**
	 * Get the east face of this element.
	 * @return The east face. NULL IF SHOULD NOT BE RENDERED
	 */
	public Face getEastFace() {
		JSONObject face = faces.optJSONObject("east");
		if (face == null) {
			return null;
		}
		
		List<Vector3f> verts = Arrays.asList(new Vector3f[] {
				getGlobalPosition(new Vector3f(to.x, to.y, to.z)),
				getGlobalPosition(new Vector3f(to.x, from.y, to.z)),
				getGlobalPosition(new Vector3f(to.x, from.y, from.z)),
				getGlobalPosition(new Vector3f(to.x, to.y, from.z)),

		});
		
		List<Vector3f> normals = Arrays.asList(new Vector3f[] {
				getGlobalRotation(new Vector3f(1, 0, 0)),
				getGlobalRotation(new Vector3f(1, 0, 0)),
				getGlobalRotation(new Vector3f(1, 0, 0)),
				getGlobalRotation(new Vector3f(1, 0, 0)),
		});
		
		return createFace(verts, normals, face);
	}
	
	/**
	 * Get the global position of a point with the element rotation taken into account.
	 * @param point Position of the point WITHOUT element rotation.
	 * @return Position of the point WITH element rotation.
	 */
	protected Vector3f getGlobalPosition(Vector3f point) {
		// Get position relative to rotation root to prevent undesired translation.
		Vector3f localPoint = point.subtract(rotation.origin);
		
		return rotatedSpatial.localToWorld(localPoint, null);		
	}
	
	/**
	 * Get the global rotation of a vector with the element rotation taken into account.
	 * @param in Vector WITHOUT element rotation.
	 * @return Vector WITH element rotation.
	 */
	protected Vector3f getGlobalRotation(Vector3f in) {
		return rotatedSpatial.localToWorld(in, null);
	}
	
	/**
	 * Finish the process of creating a face from a list of vertices.
	 * @param vertices The input vertices going counterclockwise.
	 * @param normals The corresponding normals to the vertices.
	 * @param face The JSON object representing the face in the file.
	 * @return The generated values with respect to the input array.
	 */
	protected Face createFace(List<Vector3f> vertices, List<Vector3f> normals, JSONObject face) {
		Face ret = new Face();
		ret.texCoords = new ArrayList<Vector2f>();
		ret.vertices = vertices;
		ret.normals = normals;
		
		JSONArray texCoords = face.optJSONArray("uv");
		
		// Add UVs
		if (texCoords != null) {
			
			Vector2f vec1 = new Vector2f(texCoords.getFloat(0), texCoords.getFloat(1));
			Vector2f vec2 = new Vector2f(texCoords.getFloat(2), texCoords.getFloat(3));
			
			Vector2f vector1 = new Vector2f(vec2.x, vec1.y).divide(16);
			Vector2f vector2 = vec1.divide(16);
			Vector2f vector3 = new Vector2f(vec1.x, vec2.y).divide(16);
			Vector2f vector4 = vec2.divide(16);
			
			// Workaround for a bug causing uvs to be upsidedown
			ret.texCoords.add(new Vector2f(vector2.x, 1-vector2.y));
			ret.texCoords.add(new Vector2f(vector3.x, 1-vector3.y));
			ret.texCoords.add(new Vector2f(vector4.x, 1-vector4.y));
			ret.texCoords.add(new Vector2f(vector1.x, 1-vector1.y));
			
			int rotation = 0;
			if (face.has("rotation")) {
				rotation = (int) (face.optFloat("rotation") / 90);
			}	
			
			cycleList(ret.texCoords, rotation);
			
			
		} else {
			ret.texCoords.add(new Vector2f(0, 0));
			ret.texCoords.add(new Vector2f(1, 0));
			ret.texCoords.add(new Vector2f(1, 1));
			ret.texCoords.add(new Vector2f(0, 1));
		}		
		
		ret.indices = new ArrayList<Integer>();
		
		ret.indices.addAll(Arrays.asList(new Integer[] {0,1,3}));
		ret.indices.addAll(Arrays.asList(new Integer[] {3,1,2}));
		
		
		// Parse cullface.
		String cullfaceString = face.optString("cullface");
		
		if (cullfaceString.matches("up")) {
			ret.cullFace = CullFace.UP;
		} else if (cullfaceString.matches("down")) {
			ret.cullFace = CullFace.DOWN;
		} else if (cullfaceString.matches("east")) {
			ret.cullFace = CullFace.EAST;
		} else if (cullfaceString.matches("west")) {
			ret.cullFace = CullFace.WEST;
		} else if (cullfaceString.matches("north")) {
			ret.cullFace = CullFace.NORTH;
		} else if (cullfaceString.matches("south")) {
			ret.cullFace = CullFace.SOUTH;
		} else {
			ret.cullFace = CullFace.NONE;
		}
		
		return ret;
	}
	
	/**
	 * Called on construction to setup the rotation of the internal spatial
	 */
	private void setupRotation() {
		if (rotation != null) {
			if (rotation.axis == RotationAxis.X) {
				rotatedSpatial.rotate((float) Math.toRadians(rotation.angle), 0, 0);
			} else if (rotation.axis == RotationAxis.Y) {
				rotatedSpatial.rotate(0, (float) Math.toRadians(rotation.angle), 0);
			} else if (rotation.axis == RotationAxis.Z) {
				rotatedSpatial.rotate(0, 0, (float) Math.toRadians(rotation.angle));
			}
			
			rotatedSpatial.setLocalTranslation(rotation.origin);
		}
	}
	
	private static Vector3f jsonArray3ToVector3(JSONArray array) {
		return new Vector3f(array.getFloat(0), array.getFloat(1), array.getFloat(2));
	}
	
	private static void cycleList(List<Vector2f> list, int amount) {
		if (amount == 0) {
			return;
		} else {
			List<Vector2f> templist = new ArrayList<Vector2f>(list);
			if (amount == 1) {
				list.set(0, templist.get(1));
				list.set(1, templist.get(2));
				list.set(2, templist.get(3));
				list.set(3, templist.get(0));
			} else if (amount == 2) {
				list.set(0, templist.get(2));
				list.set(1, templist.get(3));
				list.set(2, templist.get(0));
				list.set(3, templist.get(1));
			} else if (amount == 3) {
				list.set(0, templist.get(3));
				list.set(1, templist.get(0));
				list.set(2, templist.get(1));
				list.set(3, templist.get(2));
			}
		}
	}
}