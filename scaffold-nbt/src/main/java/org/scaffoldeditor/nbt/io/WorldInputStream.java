package org.scaffoldeditor.nbt.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import com.github.mryurihi.tbnbt.stream.NBTInputStream;
import com.github.mryurihi.tbnbt.tag.NBTTagCompound;

/**
 * This class can read and parse Minecraft Region files,
 * outputting them into a series of CompoundMaps.
 * @author Sam54123
 */
public class WorldInputStream implements Closeable {

	private static final int SECTOR_SIZE = 4096;

	/**
	 * Contains chunk location information defined in the header of the file.
	 */
	protected class ChunkLocation implements Comparable<ChunkLocation> {
		/**
		 * Chunk offset from the start of the file.
		 */
		public final int offset;
		
		/**
		 * The length of the chunk
		 */
		public final int length;
		
		/**
		 * For internal use only.
		 * DO NOT CALL MANUALLY.
		 */
		public ChunkLocation(int offset, int length) {
			this.offset = offset;
			this.length = length;
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
	
	private final DataInputStream is;
	
	// The head in the chunkLocations array.
	private int locationHead = 0;
	
	public final List<ChunkLocation> chunkLocations = new ArrayList<ChunkLocation>();

	/**
	 * Create a new WorldInputStream.
	 * @param is Input Stream to read.
	 * @throws IOException 
	 */
	public WorldInputStream(InputStream is) throws IOException {
		this.is = new DataInputStream(is);

		for (int index = 0; index < 1024; index++) {
			int entry = this.is.readInt();

			if (entry != 0) {
				int chunkOffset = entry >>> 8;
				int chunkSize = entry & 15;

                ChunkLocation chunkLocation = new ChunkLocation(chunkOffset * SECTOR_SIZE, chunkSize * SECTOR_SIZE);
                chunkLocations.add(chunkLocation);
			}
		}

		chunkLocations.sort(ChunkLocation::compareTo);

		// Skip timestamps and go to the first chunk
        if (!chunkLocations.isEmpty()) {
            is.skip(chunkLocations.get(0).offset - 4096);
        }
	}
	
	/**
	 * Read a chunk from the file, and advance the head to the next chunk.
	 * @return Chunk Information
	 * @throws IOException If an IOException occurs during reading the NBT.
	 */
	public ChunkNBTInfo readChunkNBT() throws IOException {
		if (locationHead == chunkLocations.size()) {
			return null;
		}

		ChunkLocation location = chunkLocations.get(locationHead);

		int length = is.readInt();
		byte compressionType = is.readByte();
		
		// Decompress chunk.
		Inflater inflater;
		if (compressionType == 1) {
			inflater = new Inflater(true);
		} else {
			inflater = new Inflater();
		}
		
		@SuppressWarnings("resource") // Root input stream still needs to be accessed.
		NBTInputStream nbtIs = new NBTInputStream(new InflaterInputStream(is, inflater, length - 1), false);
		NBTTagCompound map = (NBTTagCompound) nbtIs.readTag();

		locationHead++;

		// Read till the next usable sector
		if (locationHead != chunkLocations.size()) {
			ChunkLocation nextLocation = chunkLocations.get(locationHead);
			is.skip(nextLocation.offset - (location.offset + (length + 4)));
		}

		NBTTagCompound levelTag = map.get("Level").getAsTagCompound();
		
		return new ChunkNBTInfo(map, length + 4, (short) levelTag.get("xPos").getAsTagInt().getValue(), (short) levelTag.get("zPos").getAsTagInt().getValue());
	}

	/**
	 * Checks whether or not there are more chunks
	 * left to be read
	 *
	 * @return true if there are, false otherwise
	 */
	public boolean hasNext() {
		return locationHead < chunkLocations.size();
	}

	@Override
	public void close() throws IOException {
		is.close();
	}
}
