package org.scaffoldeditor.nbt.math;

/**
 * A variety of math utility functions.
 */
public final class MathUtils {
	private MathUtils() {};

	/**
	 * Obtain the point at which a ray intercepts a plane.
	 * 
	 * @param rayDirection A vector representing the direction of the ray.
	 * @param rayPoint     The ray's starting point.
	 * @param planeNormal  The normal vector of the plane (the direction it's
	 *                     pointing).
	 * @param planePoint   A point on the plane.
	 * @return The point at which they intercept.
	 * @author Adapted from <a href=
	 *         "https://www.rosettacode.org/wiki/Find_the_intersection_of_a_line_with_a_plane#Java">https://www.rosettacode.org/wiki/Find_the_intersection_of_a_line_with_a_plane#Java</a>
	 */
	public static Vector3d intersectPoint(Vector3d rayDirection, Vector3d rayPoint, Vector3d planeNormal,
			Vector3d planePoint) {
		Vector3d diff = rayPoint.subtract(planePoint);
		double prod1 = diff.dot(planeNormal);
		double prod2 = rayDirection.dot(planeNormal);
		double prod3 = prod1 / prod2;
		return rayPoint.subtract(rayDirection.multiply(prod3));
	}
}
