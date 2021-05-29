package org.scaffoldeditor.nbt.block.transform;

import java.util.Iterator;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.math.Matrix;
import org.scaffoldeditor.nbt.math.Vector3d;
import org.scaffoldeditor.nbt.math.Vector3i;

/**
 * A block collection that represents another block collection offset by a certian transform.
 * Does not copy the underlying block collection. If the underlying connection is mutable,
 * changes to it will be reflected here.
 * @author Igrium
 */
public class TransformBlockCollection implements BlockCollection {
	
	private final BlockCollection base;
	protected final Matrix transformMatrix;
	
	/**
	 * Create a transform block collection.
	 * @param base Base block collection. If this is also a transform block collection,
	 * the base of this one will be used and the matrix multiplied by the given transform matrix.
	 * @param transformMatrix Transformation matrix to use.
	 */
	public TransformBlockCollection(BlockCollection base, Matrix transformMatrix) {
		// Don't overload memory with a bunch of nested transform block collections. Just generate a new matrix.
		if (base instanceof TransformBlockCollection) {
			this.base = ((TransformBlockCollection) base).base;
			this.transformMatrix = ((TransformBlockCollection) base).transformMatrix.times(transformMatrix);
		} else {
			this.base = base;
			this.transformMatrix = transformMatrix;
		}	
	}

	@Override
	public Block blockAt(int x, int y, int z) {
		Vector3d inVector = new Vector3d(x, y, z);
		return getBase().blockAt(transformVector(inVector).floor());
	}
	
	@Override
	public boolean hasBlock(int x, int y, int z) {
		Vector3d inVector = new Vector3d(x, y, z);
		return getBase().hasBlock(transformVector(inVector).floor());
	}
	
	/**
	 * Transform a vector according to the transform matrix.
	 * @param in Coordinates in relation to this block collection.
	 * @return Coordinates in relation to the base block collection.
	 */
	public Vector3d transformVector(Vector3d in) {
		Matrix inMatrix = Matrix.fromVector(in);
		Matrix outMatrix = transformMatrix.times(inMatrix);
		return outMatrix.toVector();
	}
	
	/**
	 * Transform a vector according to the transform matrix.
	 * @param in Coordinates in relation to this block collection.
	 * @return Coordinates in relation to the base block collection.
	 */
	public Vector3d transformVector(Vector3i in) {
		return transformVector(in.toDouble());
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
	public Matrix getMatrix() {
		return transformMatrix;
	}

	@Override
	public Iterator<Vector3i> iterator() {
		return new Iterator<Vector3i>() {
			
			Iterator<Vector3i> baseIterator = base.iterator();
			
			@Override
			public Vector3i next() {
				return transformVector(baseIterator.next()).floor();
			}
			
			@Override
			public boolean hasNext() {
				return baseIterator.hasNext();
			}
		};
	}

}
