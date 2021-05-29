package org.scaffoldeditor.nbt.math;

import java.util.Objects;

/**
 * 3D float Vector implementation
 */
public class Vector3f {
		
	public final float x;
	public final float y;
	public final float z;
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	public float lengthSquared() {
		return (x*x + y*y + z*z);
	}
	
	public double length() {
		return Math.sqrt(lengthSquared());
	}
	
	
	@Override
	public String toString() {
		return "<"+x+", "+y+", "+z+">";
	}
	
	@Override
	public boolean equals(Object arg0) {
		Vector3f other = (Vector3f) arg0;
		if (other != null) {
			return (this.x == other.x && this.y == other.y && this.z == other.z);
		} else {
			return super.equals(arg0);
		}

	}
	
	public Vector3f add(Vector3f other) {
		return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
	}
	
	public Vector3f subtract(Vector3f other) {
		return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.z);
	}
	
	public Vector3f multiply(float num) {
		return new Vector3f(x * num, y * num, z * num);
	}
	
	public Vector3f divide(float num) {
		return new Vector3f(x / num, y / num, z / num);
	}
	
	public Vector3i floor() {
		return new Vector3i((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
	}
	
	public Vector3d toDouble() {
		return new Vector3d(x, y, z);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}
}