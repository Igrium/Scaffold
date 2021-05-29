package org.scaffoldeditor.nbt.math;

import java.util.Objects;

/**
 * 3D double Vector implementation
 */
public class Vector3d {
		
	public final double x;
	public final double y;
	public final double z;
	
	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public double lengthSquared() {
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
		Vector3d other = (Vector3d) arg0;
		if (other != null) {
			return (this.x == other.x && this.y == other.y && this.z == other.z);
		} else {
			return super.equals(arg0);
		}

	}
	
	public Vector3d add(Vector3d other) {
		return new Vector3d(this.x + other.x, this.y + other.y, this.z + other.z);
	}
	
	public Vector3d subtract(Vector3d other) {
		return new Vector3d(this.x - other.x, this.y - other.y, this.z - other.z);
	}
	
	public Vector3d multiply(double num) {
		return new Vector3d(x * num, y * num, z * num);
	}
	
	public Vector3d divide(double num) {
		return new Vector3d(x / num, y / num, z / num);
	}
	
	public Vector3i floor() {
		return new Vector3i((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
	}
	
	/**
	 * Cast this double vector to a float vector.
	 * May lose precision.
	 */
	public Vector3f toFloat() {
		return new Vector3f((float) x, (float) y, (float) z);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}
}