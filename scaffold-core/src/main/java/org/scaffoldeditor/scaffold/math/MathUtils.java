package org.scaffoldeditor.scaffold.math;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.util.SingleTypePair;

public final class MathUtils {
	private MathUtils() {}
	
	/**
	 * Detect a collision between two bounding boxes.
	 * @param a1 Box 1's min point.
	 * @param a2 Box 1's max point.
	 * @param b1 Box 2's min point.
	 * @param b2 Box 2's max point.
	 * @return Are they overlapping?
	 */
	public static boolean detectCollision(Vector3f a1, Vector3f a2, Vector3f b1, Vector3f b2) {
		boolean x = (a1.x <= b2.x && a2.x >= b1.x);
		boolean y = (a1.y <= b2.y && a2.y >= b1.y);
		boolean z = (a1.z <= b2.z && a2.z >= b1.z);
		return x && y && z;
	}
	
	/**
	 * Calculate the volume of a bounding box.
	 * @param a1 Box min point.
	 * @param a2 Box max point.
	 * @return Box volume.
	 */
	public static float calculateVolume(Vector3f a1, Vector3f a2) {
		float volume = ((a2.x - a1.x) * (a2.y - a1.y) * (a2.z - a1.z));
		return Math.abs(volume);
	}
	
	/**
	 * Obtain a minimum and maximum point from an arbitrary box.
	 * @param point1 Any corner of the box.
	 * @param point2 The opposite corner.
	 * @return The minimum and maximum points.
	 */
	public static SingleTypePair<Vector3f> normalizeBox(Vector3f point1, Vector3f point2) {
		float minX = Math.min(point1.x, point2.x);
		float minY = Math.min(point1.y, point2.y);
		float minZ = Math.min(point1.z, point2.z);
		Vector3f min = new Vector3f(minX, minY, minZ);
		
		float maxX = Math.max(point1.x, point2.x);
		float maxY = Math.max(point1.y, point2.y);
		float maxZ = Math.max(point1.z, point2.z);
		Vector3f max = new Vector3f(maxX, maxY, maxZ);
		
		return new SingleTypePair<Vector3f>(min, max);
	}
}
