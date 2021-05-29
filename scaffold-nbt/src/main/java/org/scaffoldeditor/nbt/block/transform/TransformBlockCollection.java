package org.scaffoldeditor.nbt.block.transform;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.math.Matrix;

/**
 * A block collection that represents another block collection offset by a certian transform.
 * Does not copy the underlying block collection. If the underlying connection is mutable,
 * changes to it will be reflected here.
 * @author Igrium
 */
public class TransformBlockCollection implements BlockCollection {
	
	private final BlockCollection base;
	private final Matrix transformMatrix;
	
	/**
	 * Create a transform block collection.
	 * @param base Base block collection.
	 * @param transformMatrix Transformation matrix to use.
	 */
	public TransformBlockCollection(BlockCollection base, Matrix transformMatrix) {
		this.base = base;
		this.transformMatrix = transformMatrix;
	}

	@Override
	public Block blockAt(int x, int y, int z) {
		Matrix in = new Matrix(new double[][] {{x}, {y}, {z}});
		Matrix outMatrix = transformMatrix.times(in);
		double[][] out = outMatrix.getData();
		double finalX = out[0][0];
		double finalY = out[1][0];
		double finalZ = out[2][0];
		
		return base.blockAt((int) Math.floor(finalX), (int) Math.floor(finalY), (int) Math.floor(finalZ));
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

}
