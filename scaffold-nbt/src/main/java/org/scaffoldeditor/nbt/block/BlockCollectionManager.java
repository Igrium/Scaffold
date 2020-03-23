package org.scaffoldeditor.nbt.block;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.scaffoldeditor.nbt.schematic.Structure;

/**
 * Manages all the block readers.
 * @author Sam54123
 */
public final class BlockCollectionManager {
	private static Map<String, BlockReader> registeredReaders = new HashMap<String, BlockReader>();
	
	/**
	 * Register a new block reader.
	 * @param reader The block reader to register.
	 * @param ext The file extension of the files to use this reader for.
	 */
	public static void registerReader(BlockReader reader, String ext) {
		registeredReaders.put(ext, reader);
	}
	
	/**
	 * Unregister a block reader.
	 * @param ext Extension of file type to unregister.
	 */
	public static void unregisterReader(String ext) {
		registeredReaders.remove(ext);
	}
	
	/**
	 * Get a block reader for a particular extension.
	 * @param ext Extension to look for.
	 * @return Block reader for extension. Null of nonexistant.
	 */
	public static BlockReader getReader(String ext) {
		return registeredReaders.get(ext);
	}
	
	/**
	 * Determine the file type and read a block collection from a file.
	 * @param file File to read.
	 * @return The parsed block collection.
	 * @throws IOException If an IO exception occurs.
	 */
	public static SizedBlockCollection readFile(File file) throws IOException {
		
		BlockReader reader = getReader(FilenameUtils.getExtension(file.getName()));
		if (reader == null) {
			throw new IOException("No block collection reader exists for file extension " + FilenameUtils.getExtension(file.getName()) + "!");
		}
		
		return reader.readBlockCollection(new FileInputStream(file));

	}
	
	/**
	 * Register the default block readers that come with Scaffold.
	 */
	public static void registerDefaults() {
		registerReader(new Structure(), "nbt");
	}
}
