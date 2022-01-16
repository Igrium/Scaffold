package org.scaffoldeditor.nbt.block;

import java.util.Iterator;

import org.joml.Vector3i;
import org.joml.Vector3ic;

/**
 * Represents an immutable block collection that has a finite amount of blocks and size
 * @author Igrium
 */
public interface SizedBlockCollection extends BlockCollection {
	
	/**
	 * Get the minimum point of the block collection (in local space)
	 */
	public Vector3ic getMin();
	
	/**
	 * Get the non-inclusive maximim point of the block collection (in local space)
	 */
	public Vector3ic getMax();
	
	/**
	 * Get the width (x) of the block collection.
	 * @return Width.
	 */
	default int getWidth() {
		return getMax().x() - getMin().x();
	}
	
	/**
	 * Get the width (y) of the block collection.
	 * @return Height.
	 */
	default int getHeight() {
		return getMax().y() - getMin().y();
	}
	
	/**
	 * Get the length (z) of the block collection.
	 * @return Length.
	 */
	default int getLength() {
		return getMax().z() - getMin().z();
	}
	
	@Override
	default Iterator<Vector3ic> iterator() {
		return new Iterator<Vector3ic>() {
			Vector3ic min = getMin();
			Vector3ic max = getMax();

			private Vector3i head = new Vector3i(min);
		

			@Override
			public boolean hasNext() {
				// Copy the head for forward iteration
				Vector3i oldHead = head;
				head = new Vector3i(oldHead);

				// Search for additional values
				boolean success = false;
				while (head.x() < max.x() && head.y() < max.y() && head.z() < max.z()) {
					if (hasBlock(head)) {
						success = true;
						break;
					} 
					iterate();
				}
				head = oldHead;

				return success;
			}

			@Override
			public Vector3i next() {
				boolean hasBlock = false;
				// Iterate until we find a non-void block
				while (!hasBlock && hasNext()) {
					hasBlock = hasBlock(head);
					iterate();
				}
				
				return head;
			}
			
			/**
			 * Move the heads to the next available slot.
			 * Scans in an X -> Z -> Y order
			 */
			private void iterate() {
				if (head.x+1 < max.x()) {
					head.x++;
				} else if (head.z+1 < max.z()) {
					head.x = min.x();
					head.z++;
				} else if (head.y < max.y()) {
					head.x = min.x();
					head.z = min.z();
					head.y++;
				}
			}
		};
	}
	
	/**
	 * Get a block collection that consists of a single block.
	 * @param block The block.
	 * @return Wrapper block collection.
	 */
	public static SizedBlockCollection singleBlock(Block block) {
		return new SizedBlockCollection() {
			
			@Override
			public Block blockAt(int x, int y, int z) {
				return block;
			}
			
			@Override
			public Vector3i getMin() {
				return new Vector3i(0, 0, 0);
			}
			
			@Override
			public Vector3i getMax() {
				return new Vector3i(1, 1, 1);
			}
		};
	}
}
