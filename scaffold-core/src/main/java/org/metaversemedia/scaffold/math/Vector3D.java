package org.metaversemedia.scaffold.math;

/**
 * Vector implementation
 */
public class Vector3D {
	private float x;
	private float y;
	private float z;
	
	public Vector3D(float x, float y, float z) {
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
	
	public static Vector3D add(Vector3D vec1, Vector3D vec2) {
		return new Vector3D(vec1.X() + vec2.X(), vec1.Y() + vec2.Y(), vec1.Z() + vec2.Z());
	}
	
	public static Vector3D subtract(Vector3D vec1, Vector3D vec2) {
		return new Vector3D(vec1.X() - vec2.X(), vec1.Y() - vec2.Y(), vec1.Z() - vec2.Z());
	}
	
	public static Vector3D multiply(Vector3D vec, float num) {
		return new Vector3D(vec.X()*num, vec.Y()*num, vec.Z()*num);
	}
	
	public static Vector3D divide(Vector3D vec, float num) {
		return new Vector3D(vec.X()/num, vec.Y()/num, vec.Z()/num);
	}
}