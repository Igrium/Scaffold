package org.scaffoldeditor.nbt.block;

import java.util.Set;

import org.joml.Vector3i;
import org.joml.Vector3ic;

/**
 * Represents a block collection that is broken into 3-dimensional sections.
 * Useful for representing large block collections such as worlds.
 * @author Igrium
 */
public interface ChunkedBlockCollection extends BlockCollection {
	
	/**
	 * Get the size of a single section along the X axis.
	 * @return Width in blocks.
	 */
	public int getSectionWidth();
	
	/**
	 * Get the size of a single section along the Z axis.
	 * @return Length in blocks.
	 */
	public int getSectionLength();
	
	/**
	 * Get the size of a single section along the Y axis.
	 * @return Height in blocks.
	 */
	public int getSectionHeight();

	/**
	 * Obtain a sized block collection representing the section at a set of
	 * coordinates. This method takes coordinates in section space. For instance, if
	 * your sections were 16x16x16 and you passed the parameters (1, 1, 0), it would
	 * return the section starting at (16, 16, 0).
	 * 
	 * @param x Section X.
	 * @param y Section Y.
	 * @param z Section Z.
	 * @return A sized block collection representing the section, or
	 *         <code>null</code> if the collection believes the section doesn't
	 *         exist.
	 */
	public SizedBlockCollection sectionAt(int x, int y, int z);
	
	/**
	 * Get a set of all the collection's sections. Although all of the sections are loaded,
	 * they may or may not be occupied.
	 */
	public Set<Vector3ic> getSections();
	
	/**
	 * Get the section coordinate that the passed vector lands in.
	 */
	default Vector3i getSection(int x, int y, int z) {
		int sectionX = (int) Math.floor((double) x/getSectionWidth());
		int sectionY = (int) Math.floor((double) y/getSectionHeight());
		int sectionZ = (int) Math.floor((double) x/getSectionLength());
		return new Vector3i(sectionX, sectionY, sectionZ);
	}
	
	/**
	 * Get the section coordinate that the passed vector lands in.
	 */
	default Vector3i getSection(Vector3ic block) {
		return getSection(block.x(), block.y(), block.z());
	}
}
