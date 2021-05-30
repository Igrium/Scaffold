package org.scaffoldeditor.scaffold.math;

import org.scaffoldeditor.nbt.math.Vector3f;

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
}
