package org.scaffoldeditor.nbt.block;

import java.util.Iterator;

import org.scaffoldeditor.nbt.math.Vector3i;

/**
 * Represents an immutable block collection that has a finite amount of blocks and size
 * @author Igrium
 */
public interface SizedBlockCollection extends BlockCollection {
	
	/**
	 * Get the minimum point of the block collection (in local space)
	 */
	public Vector3i getMin();
	
	/**
	 * Get the non-inclusive maximim point of the block collection (in local space)
	 */
	public Vector3i getMax();
	
	/**
	 * Get the width (x) of the block collection.
	 * @return Width.
	 */
	default int getWidth() {
		return getMax().x - getMin().x;
	}
	
	/**
	 * Get the width (y) of the block collection.
	 * @return Height.
	 */
	default int getHeight() {
		return getMax().y - getMin().y;
	}
	
	/**
	 * Get the length (z) of the block collection.
	 * @return Length.
	 */
	default int getLength() {
		return getMax().z - getMin().z;
	}
	
	@Override
	default Iterator<Vector3i> iterator() {
		return new Iterator<Vector3i>() {
			Vector3i min = getMin();
			Vector3i max = getMax();
			
			private int headX = min.x;
			private int headY = min.y;
			private int headZ = min.z;

			@Override
			public boolean hasNext() {
				// Backup the heads.
				int oldHeadX = this.headX;
				int oldHeadY = this.headY;
				int oldHeadZ = this.headZ;

				// Search for additional values
				boolean success = false;
				while (headX < max.x && headY < max.y && headZ < max.z) {
					if (hasBlock(headX, headY, headZ)) {
						success = true;
						break;
					}
					iterate();
				}

				headX = oldHeadX;
				headY = oldHeadY;
				headZ = oldHeadZ;

				return success;
			}

			@Override
			public Vector3i next() {
				boolean hasBlock = false;
				Vector3i out = null;
				// Iterate until we find a non-void block
				while (!hasBlock && hasNext()) {
					hasBlock = hasBlock(headX, headY, headZ);
					out = new Vector3i(headX, headY, headZ);
					iterate();
				}
				
				return out;
			}
			
			/**
			 * Move the heads to the next available slot.
			 * Scans in an X -> Z -> Y order
			 */
			private void iterate() {
				if (headX+1 < max.x) {
					headX++;
				} else if (headZ+1 < max.z) {
					headX = min.x;
					headZ++;
				} else if (headY < max.y) {
					headX = min.x;
					headZ = min.z;
					headY++;
				}
			}
		};
	}
}
