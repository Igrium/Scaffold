package org.scaffoldeditor.scaffold.math;

import org.json.JSONArray;
import org.json.JSONException;
import org.scaffoldeditor.scaffold.serialization.XMLSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 3D Vector implementation
 */
public class Vector implements XMLSerializable<Vector> {
	
	public static final String REGISTRY_NAME = "vector";
	
	public final float x;
	public final float y;
	public final float z;
	
	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
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
	
	public float length() {
		return (float) Math.sqrt(lengthSquared());
	}
	
	
	@Override
	public String toString() {
		return "<"+x+", "+y+", "+z+">";
	}
	
	@Override
	public boolean equals(Object arg0) {
		Vector other = (Vector) arg0;
		if (other != null) {
			return (this.X() == other.X() && this.Y() == other.Y() && this.Z() == other.Z());
		} else {
			return super.equals(arg0);
		}

	}
	
	public static Vector add(Vector vec1, Vector vec2) {
		return new Vector(vec1.X() + vec2.X(), vec1.Y() + vec2.Y(), vec1.Z() + vec2.Z());
	}
	
	public static Vector subtract(Vector vec1, Vector vec2) {
		return new Vector(vec1.X() - vec2.X(), vec1.Y() - vec2.Y(), vec1.Z() - vec2.Z());
	}
	
	public static Vector multiply(Vector vec, float num) {
		return new Vector(vec.X()*num, vec.Y()*num, vec.Z()*num);
	}
	
	public static Vector divide(Vector vec, float num) {
		return new Vector(vec.X()/num, vec.Y()/num, vec.Z()/num);
	}
	
	public static Vector floor(Vector vec) {
		return new Vector((float) Math.floor(vec.X()), (float) Math.floor(vec.Y()), (float) Math.floor(vec.Z()));
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
			System.out.println("Unable to load Vector from JSONArray "+array);
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