package org.scaffoldeditor.nbt.math;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector3i;

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
	public static Vector3dc intersectPoint(Vector3dc rayDirection, Vector3dc rayPoint, Vector3dc planeNormal,
			Vector3dc planePoint) {
		Vector3dc diff = rayPoint.sub(planePoint, new Vector3d());
		double prod1 = diff.dot(planeNormal);
		double prod2 = rayDirection.dot(planeNormal);
		double prod3 = prod1 / prod2;
		return rayPoint.sub(rayDirection.mul(prod3, new Vector3d()), new Vector3d());
	}

	/**
	 * Floor the components of a 3D vector and cast them to int.
	 * @param in Vector
	 * @return Int vector
	 */
	public static Vector3i floorVector(Vector3dc in) {
		return new Vector3i((int) Math.floor(in.x()), (int) Math.floor(in.y()), (int) Math.floor(in.z()));
	}

	/**
	 * Floor the components of a 3D vector and cast them to int.
	 * @param in Vector
	 * @return Int vector
	 */
	public static Vector3i floorVector(Vector3fc in) {
		return new Vector3i((int) Math.floor(in.x()), (int) Math.floor(in.y()), (int) Math.floor(in.z()));
	}
}
