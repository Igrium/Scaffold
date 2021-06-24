package org.scaffoldeditor.nbt.math;

import java.util.Objects;

/**
 * 3D int Vector implementation
 */
public class Vector3i {
		
	public final int x;
	public final int y;
	public final int z;
	
	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public int lengthSquared() {
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
		Vector3i other = (Vector3i) arg0;
		if (other != null) {
			return (this.x == other.x && this.y == other.y && this.z == other.z);
		} else {
			return super.equals(arg0);
		}

	}
	
	public Vector3i add(Vector3i other) {
		return new Vector3i(this.x + other.x, this.y + other.y, this.z + other.z);
	}
	
	public Vector3i subtract(Vector3i other) {
		return new Vector3i(this.x - other.x, this.y - other.y, this.z - other.z);
	}
	
	public Vector3i multiply(int num) {
		return new Vector3i(x * num, y * num, z * num);
	}
	
	public Vector3i divide(int num) {
		return new Vector3i(x / num, y / num, z / num);
	}
	
	public int dot(Vector3i other) {
		return x * other.x + y * other.y + z * other.z;
	}
	
	public Vector3f toFloat() {
		return new Vector3f(x, y, z);
	}
	
	public Vector3d toDouble() {
		return new Vector3d(x, y, z);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z));
	}
}