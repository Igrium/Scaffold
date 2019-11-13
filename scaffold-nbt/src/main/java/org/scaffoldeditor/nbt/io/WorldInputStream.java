package org.scaffoldeditor.nbt.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class can read and parse Minecraft Region files,
 * outputing them into a series of CompoundMaps.
 * @author Sam54123
 */
public class WorldInputStream implements Closeable {
	/**
	 * Contains chunk location information defined in the header of the file.
	 */
	public class ChunkLocation  {
		/**
		 * Chunk offset in 4KiB sectors from the start of the file.
		 */
		public final int offset;
		
		/**
		 * The length of the chunk, also in 4KiB sectors.
		 */
		public final short length;
		
		public ChunkLocation(int offset, short length) {
			this.offset = offset;
			this.length = length;
		}
		
	}
	
	private final InputStream is;
	
	// The X and Z offsets of the file.
	private int offsetX;
	private int offsetz;
	
	public List<ChunkLocation> chunkLocations = new ArrayList<ChunkLocation>();
	
	/**
	 * Create a new WorldInputStream.
	 * @param is Input Stream to read.
	 * @throws IOException 
	 */
	public WorldInputStream(InputStream is) throws IOException {
		this.is = is;
		
		// Load chunk data.
		int x = 0;
		int z = 0;
		
		// Iterate over chunk array
		while (x < 32 && z < 32) {
			byte[] bytes = new byte[4];
			is.read(bytes);
			System.out.println(Arrays.toString(bytes));
			
			int loc = (bytes[2] & 0xFF) | ((bytes[1] & 0xFF) << 8) | ((bytes[0] & 0x0F) << 16);
			
			short length = bytes[3];
			
			chunkLocations.add(new ChunkLocation(loc, length));
			
			// Increment chunk heads
			if (x == 31) {
				x = 0;
				z++;
			} else {
				x++;
			}
		}
		
	}

	@Override
	public void close() throws IOException {
		is.close();
	}
}
