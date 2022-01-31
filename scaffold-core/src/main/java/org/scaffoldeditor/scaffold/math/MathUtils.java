package org.scaffoldeditor.scaffold.math;

import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.scaffoldeditor.nbt.util.SingleTypePair;

public final class MathUtils {
	private MathUtils() {}

	public static final Matrix4dc NORTH = new Matrix4d();
	public static final Matrix4dc EAST = new Matrix4d().rotate(Math.toRadians(90), 0, 1, 0);
	public static final Matrix4dc SOUTH = new Matrix4d().rotate(Math.toRadians(180), 0, 1, 0);
	public static final Matrix4dc WEST = new Matrix4d().rotate(Math.toRadians(270), 0, 1, 0);
	
	/**
	 * Detect a collision between two bounding boxes.
	 * @param a1 Box 1's min point.
	 * @param a2 Box 1's max point.
	 * @param b1 Box 2's min point.
	 * @param b2 Box 2's max point.
	 * @return Are they overlapping?
	 */
	public static boolean detectCollision(Vector3dc a1, Vector3dc a2, Vector3dc b1, Vector3dc b2) {
		boolean x = (a1.x() <= b2.x() && a2.x() >= b1.x());
		boolean y = (a1.y() <= b2.y() && a2.y() >= b1.y());
		boolean z = (a1.z() <= b2.z() && a2.z() >= b1.z());
		return x && y && z;
	}
	
	/**
	 * Calculate the volume of a bounding box.
	 * @param a1 Box min point.
	 * @param a2 Box max point.
	 * @return Box volume.
	 */
	public static double calculateVolume(Vector3dc a1, Vector3dc a2) {
		double volume = (a2.x() - a1.x()) * (a2.y() - a1.y()) * (a2.z() - a1.z());
		return Math.abs(volume);
	}

	/**
	 * Calculate the volume of a bounding box.
	 * @param a1 Box min point.
	 * @param a2 Box max point.
	 * @return Box volume.
	 */
	public static double calculateVolume(Vector3ic a1, Vector3ic a2) {
		double volume = (a2.x() - a1.x()) * (a2.y() - a1.y()) * (a2.z() - a1.z());
		return Math.abs(volume);
	}
	
	/**
	 * Obtain a minimum and maximum point from an arbitrary box.
	 * @param point1 Any corner of the box.
	 * @param point2 The opposite corner.
	 * @return The minimum and maximum points.
	 */
	public static SingleTypePair<Vector3dc> normalizeBox(Vector3dc point1, Vector3dc point2) {
		return new SingleTypePair<Vector3dc>(point1.min(point2, new Vector3d()), point1.max(point2, new Vector3d()));
	}
	
	/**
	 * Get the entity angle of a vector. Equivalent to <code>execute facing</code>
	 * where <code>delta</code> is the difference between the target and the
	 * executor vectors.
	 * 
	 * @param delta Rotation vector.
	 * @return A two-element array indicating the calculated yaw and pitch in a
	 *         format that can be plugged into Minecraft entities.
	 */
	public static double[] getFacingAngle(Vector3dc delta) {
		double len = Math.sqrt(delta.x() * delta.x() + delta.z() * delta.z());
		// This is backwards. Don't ask me why.
		double pitch = wrapDegrees(-Math.toDegrees(Math.atan2(delta.y(), len)));
		double yaw = wrapDegrees(Math.toDegrees(Math.atan2(delta.z(), delta.x())) - 90);
		return new double[] { yaw, pitch };
	}
	
	/**
	 * Wraps an angle in degrees to the interval {@code [-180, 180)}. Equivalent to the Minecraft method of the same name.
	 * @param degrees Input angle in degrees.
	 * @return Wrapped angle.
	 */
	public static double wrapDegrees(double degrees) {
		degrees = degrees % 360;
		if (degrees >= 180) {
			degrees -= 360;
		}
		if (degrees < -180) {
			degrees += 360;
		}
		return degrees;
	}

	/**
	 * Floor the components of a 3D vector and cast them to int.
	 * @param in Vector
	 * @return Int vector
	 */
	public static Vector3i floorVector(Vector3dc in) {
		return org.scaffoldeditor.nbt.math.MathUtils.floorVector(in);
	}

	/**
	 * Floor the components of a 3D vector and cast them to int.
	 * @param in Vector
	 * @return Int vector
	 */
	public static Vector3i floorVector(Vector3fc in) {
		return org.scaffoldeditor.nbt.math.MathUtils.floorVector(in);
	}
}
