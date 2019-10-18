package org.scaffoldeditor.editor.editor3d.util;

import org.scaffoldeditor.scaffold.math.Vector;

import com.jme3.math.Vector3f;

/**
 * A variety of utility functions to assist in the 3D section of the editor.
 * @author Sam54123
 */
public final class EditorUtils {
	/**
	 * Converts a Scaffold vector to a JMonkeyEngine vector.
	 * @param vector Scaffold vector.
	 * @return JMonkeyEngine vector.
	 */
	public static Vector3f sVectorToMVector(Vector vector) {
		return new Vector3f(vector.X(), vector.Y(), vector.Z());
	}
	
	/**
	 * Converts a JMonkeyEngine vector to a Scaffold vector.
	 * @param vector JMonkeyEngine vector.
	 * @return Scaffold vector.
	 */
	public static Vector mVectorToSVector(Vector3f vector) {
		return new Vector(vector.getX(), vector.getY(), vector.getZ());
	}
}
