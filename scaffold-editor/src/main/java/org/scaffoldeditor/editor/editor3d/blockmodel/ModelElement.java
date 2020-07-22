package org.scaffoldeditor.editor.editor3d.blockmodel;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

/**
 * Represents a single model element.
 * @author Igrium
 * @see <a href=https://minecraft.gamepedia.com/Model#Block_models>Model Format</a>
 */
public class ModelElement {
	
	public enum RotationAxis {
		X, Y, Z
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
		public float angle;
		
		/**
		 * Specifies whether or not to scale the faces across the whole block. Can be true or false. Defaults to false.
		 */
		public boolean rescale;
	}
	

	/**
	 * Create a model element.
	 * @param from Start point of the cube.
	 * @param to End point of the cube.
	 * @param rotation Rotation of the element.
	 * @param faces Holds all the faces of the cube. If a face is left out, it does not render.
	 */
	public ModelElement(Vector3f from, Vector3f to, ElementRotation rotation, JSONObject faces) {
		
	}
	
	/**
	 * Create a model element from a JSON object.
	 * @param element Element JSON object.
	 */
	public ModelElement(JSONObject element) {
		this(jsonArrayToVector(element.getJSONArray("from")), jsonArrayToVector(element.getJSONArray("to")),
				null, element.getJSONObject("faces"));
		
		ElementRotation rotation = new ElementRotation();
		JSONObject rotationTag = element.getJSONObject("rotation");
		rotation.origin = jsonArrayToVector(rotationTag.getJSONArray("origin"));
		rotation.angle = element.getFloat("angle");
		rotation.rescale = element.getBoolean("rescale");
		
		String axisString = element.getString(element.getString("axis"));
		if (axisString == "x") {
			rotation.axis = RotationAxis.X;
		} else if (axisString == "y") {
			rotation.axis = RotationAxis.Y;
		} else if (axisString == "z") {
			rotation.axis = RotationAxis.Z;
		}
		
		
	}
	
	/**
	 * The JSON object containing all the face data.
	 */
	protected JSONObject faces;
	
	private static Vector3f jsonArrayToVector(JSONArray array) {
		return new Vector3f(array.getFloat(0), array.getFloat(1), array.getFloat(2));
	}
}