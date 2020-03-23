package org.scaffoldeditor.nbt.block;

import java.io.IOException;
import java.io.InputStream;

/**
 * A block reader is a class that is able to read a file and return a sized block collection.
 * @author Sam54123
 */
public interface BlockReader {
	/**
	 * Read a Block Collection from an input stream
	 * @param in The input stream to read.
	 * @return The parsed block collection.
	 * @throws IOException if an IO exception occurs.
	 */
	public SizedBlockCollection readBlockCollection(InputStream in) throws IOException;
}
