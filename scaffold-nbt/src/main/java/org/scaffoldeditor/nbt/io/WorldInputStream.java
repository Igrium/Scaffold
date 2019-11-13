package org.scaffoldeditor.nbt.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.flowpowered.nbt.CompoundMap;

/**
 * This class can read and parse Minecraft Region files,
 * outputting them into a series of CompoundMaps.
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
		
		/**
		 * The X position in the region file of the chunk.
		 */
		public final short x;
		
		/**
		 * The Z position in the region file of the chunk.
		 */
		public final short z;
		
		public ChunkLocation(int offset, short length, short x, short z) {
			this.offset = offset;
			this.length = length;
			this.x = x;
			this.z = z;
		}
		
	}
	
	/**
	 * Represents data about a chunk once it's been read.
	 */
	public class ChunkNBTInfo {
		
		/**
		 * The chunk's NBT data.
		 */
		public final CompoundMap NBT;
		
		/**
		 * How many bytes were read while reading the chunk?
		 */
		public final int bytesRead;
		
		/**
		 * The chunk's X position in the file.
		 */
		public final short x;
		
		/**
		 * The chunk's Z position in the file.
		 */
		public final short z;
		
		/**
		 * For internal use only.
		 * DO NOT CALL MANUALLY.
		 */
		public ChunkNBTInfo(CompoundMap NBT, int bytesRead, short x, short z) {
			this.NBT = NBT;
			this.bytesRead = bytesRead;
			this.x = x;
			this.z = z;
		}
	}
	
	private final InputStream is;
	
	// The X and Z offsets of the file.
	private int offsetX;
	private int offsetz;
	
	// The current head location.
	private short headX;
	private short headZ;
	
	// The head in the chunkLocations array.
	private int locationHead;
	
	public final List<ChunkLocation> chunkLocations = new ArrayList<ChunkLocation>();
	
	/**
	 * Create a new WorldInputStream.
	 * @param is Input Stream to read.
	 * @throws IOException 
	 */
	public WorldInputStream(InputStream is) throws IOException {
		this.is = is;
		
		// Load chunk data.
		short x = 0;
		short z = 0;
		
		// Iterate over chunk array and read location entries.
		while (x < 32 && z < 32) {
			byte[] bytes = new byte[4];
			is.read(bytes);
			
			int loc = (bytes[2] & 0xFF) | ((bytes[1] & 0xFF) << 8) | ((bytes[0] & 0x0F) << 16);
			short length = bytes[3];
			
			chunkLocations.add(new ChunkLocation(loc, length, x, z));
			
			// Increment chunk heads
			if (x == 31) {
				x = 0;
				z++;
			} else {
				x++;
			}
		}
		
		// Skip past timestamps
		is.skip(4096);
	}
	
//	// Read the next chunk from the file.
//	public ChunkNBTInfo readChunkNBT() {
//		
//	}
	
	/**
	 * Increment the head to the next position.
	 */
	private void increment() {
		if (headX == 31) {
			headX = 0;
			headZ++;
		} else {
			headX++;
		}
	}

	@Override
	public void close() throws IOException {
		is.close();
	}
}
