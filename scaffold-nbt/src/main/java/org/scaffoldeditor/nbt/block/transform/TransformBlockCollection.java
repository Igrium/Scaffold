package org.scaffoldeditor.nbt.block.transform;

import java.util.Iterator;

import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3ic;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.math.MathUtils;

/**
 * A block collection that represents another block collection offset by a certian transform.
 * Does not copy the underlying block collection. If the underlying connection is mutable,
 * changes to it will be reflected here.
 * @author Igrium
 */
public class TransformBlockCollection implements BlockCollection {
	
	private final BlockCollection base;
	protected final Matrix4dc transformMatrix;
	
	/**
	 * Create a transform block collection.
	 * @param base Base block collection. If this is also a transform block collection,
	 * the base of this one will be used and the matrix multiplied by the given transform matrix.
	 * @param transformMatrix Transformation matrix to use.
	 */
	public TransformBlockCollection(BlockCollection base, Matrix4dc transformMatrix) {
		// Don't overload memory with a bunch of nested transform block collections. Just generate a new matrix.
		if (base instanceof TransformBlockCollection) {
			this.base = ((TransformBlockCollection) base).base;
			this.transformMatrix = transformMatrix.mul(((TransformBlockCollection) base).transformMatrix, new Matrix4d());
		} else {
			this.base = base;
			this.transformMatrix = transformMatrix;
		}	
	}

	@Override
	public Block blockAt(int x, int y, int z) {
		Vector3d inVector = new Vector3d(x, y, z);
		return getBase().blockAt(MathUtils.floorVector(transformVector(inVector)));
	}
	
	@Override
	public boolean hasBlock(int x, int y, int z) {
		Vector3d inVector = new Vector3d(x, y, z);
		return getBase().hasBlock(MathUtils.floorVector(transformVector(inVector)));
	}
	
	/**
	 * Transform a vector according to the transform matrix.
	 * @param in Coordinates in relation to this block collection.
	 * @return Coordinates in relation to the base block collection.
	 */
	public Vector3d transformVector(Vector3dc in) {
		return transformVector(in, new Vector3d());
	}

	/**
	 * Transform a vector according to the transform matrix.
	 * @param in Coordinates in relation to this block collection.
	 * @param dest Will hold the result.
	 * @return dest
	 */
	public Vector3d transformVector(Vector3dc in, Vector3d dest) {
		return transformMatrix.transformPosition(in, dest);
	}
	
	/**
	 * Transform a vector according to the transform matrix.
	 * @param in Coordinates in relation to this block collection.
	 * @return Coordinates in relation to the base block collection.
	 */
	public Vector3d transformVector(Vector3ic in) {
		return transformMatrix.transformPosition(new Vector3d(in.x(), in.y(), in.z()));
	}
	
	/**
	 * Get the base block collection.
	 */
	public BlockCollection getBase() {
		return base;
	}
	
	/**
	 * Get the transform matrix in use.
	 * @return
	 */
	public Matrix4dc getMatrix() {
		return transformMatrix;
	}

	@Override
	public Iterator<Vector3ic> iterator() {
		return new Iterator<Vector3ic>() {
			
			Iterator<Vector3ic> baseIterator = base.iterator();
			
			@Override
			public Vector3ic next() {
				return MathUtils.floorVector(transformVector(baseIterator.next()));
			}
			
			@Override
			public boolean hasNext() {
				return baseIterator.hasNext();
			}
		};
	}

}
