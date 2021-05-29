package org.scaffoldeditor.nbt.block.transform;

import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.math.Matrix;
import org.scaffoldeditor.nbt.math.Vector3i;

/**
 * Implements the {@link SizedBlockCollection} methods on {@link TransformBlockCollection}
 * <br>
 * Note the get min/max functions do not currently work w rotations not on 90 degree increments.
 * @author Igrium
 *
 */
public class TransformSizedBlockCollection extends TransformBlockCollection implements SizedBlockCollection {
	
	protected final SizedBlockCollection base;

	public TransformSizedBlockCollection(SizedBlockCollection base, Matrix transformMatrix) {
		super(base, transformMatrix);
		this.base = base;
	}
	
	@Override
	public SizedBlockCollection getBase() {
		return base;
	}

	@Override
	public Vector3i getMin() {
		Vector3i baseMin = getBaseMin();
		Vector3i baseMax = getBaseMax();
		return new Vector3i(Math.min(baseMin.x, baseMax.x), Math.min(baseMin.y, baseMax.y),
				Math.min(baseMin.z, baseMax.z));
	}

	@Override
	public Vector3i getMax() {
		Vector3i baseMin = getBaseMin();
		Vector3i baseMax = getBaseMax();
		return new Vector3i(Math.max(baseMin.x, baseMax.x), Math.max(baseMin.y, baseMax.y),
				Math.max(baseMin.z, baseMax.z));
	}
	
	/**
	 * Get what the base determines to be the min point with the transform applied.
	 */
	private Vector3i getBaseMin() {
		return transformVector(getBase().getMin().toDouble()).floor();
	}
	
	/**
	 * Get what the base determines to be the max point with the transform applied.
	 */
	private Vector3i getBaseMax() {
		return transformVector(getBase().getMax().toDouble()).floor();
	}
}
