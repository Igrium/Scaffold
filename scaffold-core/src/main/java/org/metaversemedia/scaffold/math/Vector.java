package org.metaversemedia.scaffold.math;

/**
 * 3D Vector implementation
 */
public class Vector {
	private float x;
	private float y;
	private float z;
	
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
}