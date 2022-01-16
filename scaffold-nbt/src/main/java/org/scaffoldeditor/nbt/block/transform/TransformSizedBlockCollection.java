package org.scaffoldeditor.nbt.block.transform;

import org.joml.Matrix4dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.math.MathUtils;

/**
 * Implements the {@link SizedBlockCollection} methods on {@link TransformBlockCollection}
 * <br>
 * Note the get min/max functions do not currently work w rotations not on 90 degree increments.
 * @author Igrium
 *
 */
public class TransformSizedBlockCollection extends TransformBlockCollection implements SizedBlockCollection {
	
	protected final SizedBlockCollection base;

	public TransformSizedBlockCollection(SizedBlockCollection base, Matrix4dc transformMatrix) {
		super(base, transformMatrix);
		this.base = base;
	}
	
	@Override
	public SizedBlockCollection getBase() {
		return base;
	}

	@Override
	public Vector3ic getMin() {
		return getBaseMin().min(getBaseMax());
	}

	@Override
	public Vector3i getMax() {
		return getBaseMin().max(getBaseMax());
	}
	
	/**
	 * Get what the base determines to be the min point with the transform applied.
	 */
	private Vector3i getBaseMin() {
		return MathUtils.floorVector(transformVector(getBase().getMin()));
	}
	
	/**
	 * Get what the base determines to be the max point with the transform applied.
	 */
	private Vector3i getBaseMax() {
		return MathUtils.floorVector(transformVector(getBase().getMax()));
	}
}
