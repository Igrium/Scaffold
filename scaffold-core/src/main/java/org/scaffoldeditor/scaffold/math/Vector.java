package org.scaffoldeditor.scaffold.math;

import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.serialization.XMLSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Wrapper around {@link Vector3f} allowing it to be serialized
 * and used in attributes.
 * @author Igrium
 * @deprecated use {@link Vector3f} instead.
 */
public class Vector extends Vector3f implements XMLSerializable {
	
	public static final String REGISTRY_NAME = "vector";
	
	public Vector(float x, float y, float z) {
		super(x, y, z);
	}
	
	public Vector(Vector3f vec) {
		this(vec.x, vec.y, vec.z);
	}

	public float X() {
		return x;
	}
	
	public float Y() {
		return y;
	}
	
	public float Z() {
		return z;
	}
	
	public float lengthSquared() {
		return (x*x + y*y + z*z);
	}
	
	public static Vector add(Vector vec1, Vector vec2) {
		return new Vector(vec1.add(vec2));
	}
	
	public static Vector subtract(Vector vec1, Vector vec2) {
		return new Vector(vec1.subtract(vec2));
	}
	
	public static Vector multiply(Vector vec, float num) {
		return new Vector(vec.multiply(num));
	}
	
	public static Vector divide(Vector vec, float num) {
		return new Vector(vec.divide(num));
	}
	
	public static Vector floor(Vector vec) {
		return new Vector(vec.floor().toFloat());
	}
	
	
	/**
	 * Serialize this vector into a JSONArray (for map saving)
	 * @return Serialized JSONArray
	 */
	public JSONArray toJSONArray() {
		return new JSONArray(new float[]{x,y,z});
	}
	
	/**
	 * Load vector from JSONArray
	 * @param array JSONArray to load from
	 * @return Vector
	 */
	public static Vector fromJSONArray(JSONArray array) {
		// JSONArray might not be formatted properly.
		try {
			float x = array.getFloat(0);
			float y = array.getFloat(1);
			float z = array.getFloat(2);
			
			return new Vector(x,y,z);
			
		} catch (JSONException e) {
			LogManager.getLogger().error("Unable to load Vector from JSONArray "+array);
			return null;
		}

	}

	@Override
	public Element serialize(Document document) {
		Element element = document.createElement(REGISTRY_NAME);
		element.setAttribute("x", String.valueOf(x));
		element.setAttribute("y", String.valueOf(y));
		element.setAttribute("z", String.valueOf(z));
		return element;
	}
	
	public static Vector deserialize(Element xml) {
		return new Vector(Float.valueOf(xml.getAttribute("x")), Float.valueOf(xml.getAttribute("y")), Float.valueOf(xml.getAttribute("z")));
	}

}