package org.scaffoldeditor.nbt.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import mryurihi.tbnbt.stream.NBTInputStream;
import mryurihi.tbnbt.tag.NBTTagCompound;

/**
 * This class can read and parse Minecraft Region files,
 * outputting them into a series of CompoundMaps.
 * @author Sam54123
 */
public class WorldInputStream implements Closeable {
	/**
	 * Contains chunk location information defined in the header of the file.
	 */
	protected class ChunkLocation implements Comparable<ChunkLocation> {
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
		
		/**
		 * For internal use only.
		 * DO NOT CALL MANUALLY.
		 */
		public ChunkLocation(int offset, short length, short x, short z) {
			this.offset = offset;
			this.length = length;
			this.x = x;
			this.z = z;
		}

		@Override
		public int compareTo(ChunkLocation o) {
			return Integer.compare(offset, o.offset);
		}
		
	}
	
	/**
	 * Represents data about a chunk once it's been read.
	 */
	public class ChunkNBTInfo {
		
		/**
		 * The chunk's NBT data.
		 */
		public final NBTTagCompound nbt;
		
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
		public ChunkNBTInfo(NBTTagCompound nbt, int bytesRead, short x, short z) {
			this.nbt = nbt;
			this.bytesRead = bytesRead;
			this.x = x;
			this.z = z;
		}
	}
	
	private final InputStream is;
	
	// The head in the chunkLocations array.
	private int locationHead = 0;
	
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
		
		// Sort the chunk locations list by order in file.
		Collections.sort(chunkLocations);
		
		// Skip past timestamps
		is.skip(4096);
	}
	
	/**
	 * Read a chunk from the file, and advance the head to the next chunk.
	 * @return Chunk Information
	 * @throws IOException If an IOException occurs during reading the NBT.
	 */
	public ChunkNBTInfo readChunkNBT() throws IOException {
		ChunkLocation location = chunkLocations.get(locationHead);
		// Skip if chunk is empty
		while (location.length == 0) {
			if (locationHead < chunkLocations.size()-1) { // Make sure we have space to iterate.
				locationHead++;	
				location = chunkLocations.get(locationHead);
			} else {
				return null;
			}
		}
		
		// Read chunk header.
		byte[] lengthArray = new byte[4];
		is.read(lengthArray);
		ByteBuffer lengthBuffer = ByteBuffer.wrap(lengthArray);
		lengthBuffer.order(ByteOrder.BIG_ENDIAN);
		int length = lengthBuffer.getInt();
				
		int compressionType = is.read();
		
		System.out.println("Compression type read: "+compressionType); // TESTING ONLY
		
		// Decompress chunk.
		Inflater inflater;
		if (compressionType == 1) {
			inflater = new Inflater(true);
		} else {
			inflater = new Inflater();
		}
		
		@SuppressWarnings("resource") // Root input stream still needs to be accessed.
		NBTInputStream nbtIs = new NBTInputStream(new InflaterInputStream(is, inflater, length-1), false);
		NBTTagCompound map = (NBTTagCompound) nbtIs.readTag();
		
		// Skip to the end of the sector.
		is.skip(location.length*4096 - (length+4));
		
		if (locationHead < chunkLocations.size()) {
			locationHead++;
		}
		
		return new ChunkNBTInfo(map, length+4, location.x, location.z);
	}
	
	// Check if we have more chunks by attempting to iterate to them.
	public boolean hasNext() {
		if (!(locationHead < chunkLocations.size())) {
			return false;
		}
		
		ChunkLocation location = chunkLocations.get(locationHead);
		while (location.length == 0) {
			if (locationHead < chunkLocations.size()-1) { // Make sure we have space to iterate.
				locationHead++;	
				location = chunkLocations.get(locationHead);
			} else {
				return false;
			}
		}
		
		// The loop only ends when we've found another location.
		// If we've found one, we know we have a next location.
		return true;
	}

	@Override
	public void close() throws IOException {
		is.close();
	}
}
