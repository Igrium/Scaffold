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
		public final short offset;
		
		/**
		 * The length of the chunk, also in 4KiB sectors.
		 */
		public final short sectorCount;
		
		public ChunkLocation(short offset, short sectorCount) {
			this.offset = offset;
			this.sectorCount = sectorCount;
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
		
		int head = 0; // Current head position, in bytes from the start of the file.
		while (head < 4096) {
			// Read location
			byte[] bytes = new byte[4];
			is.read(bytes);
			System.out.println(Arrays.toString(bytes));
			
			byte[] loc = new byte[] {bytes[0],bytes[1],bytes[2]};
			short length = bytes[3];
			
			chunkLocations.add(new ChunkLocation(ByteBuffer.wrap(loc).getShort(), length));
			head += 4;
		}
		
	}

	@Override
	public void close() throws IOException {
		is.close();
	}
}