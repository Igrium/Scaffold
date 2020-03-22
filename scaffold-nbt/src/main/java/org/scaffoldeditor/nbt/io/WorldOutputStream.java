package org.scaffoldeditor.nbt.io;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.*;

import com.github.mryurihi.tbnbt.stream.NBTInputStream;
import com.github.mryurihi.tbnbt.tag.NBTTagCompound;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;

import com.github.mryurihi.tbnbt.stream.NBTOutputStream;
import com.github.mryurihi.tbnbt.tag.NBTTag;

/**
 * An output stream to write Minecraft Region files.
 * This output stream is buffered due to the nature of the header.
 * <br>
 * Read more about Region files on the <a href="https://minecraft.gamepedia.com/Region_file_format">Minecraft Wiki.</a>
 * @author Sam54123
 */
public class WorldOutputStream implements Closeable {
	
	/**
	 * How wide a region file is, in chunks.
	 */
	public final int SIZEX = 32;
	
	/**
	 * How long a region file is, in chunks.
	 */
	public final int SIZEZ = 32;
	
	/**
	 * Master output stream of the world output stream.
	 */
	protected final DataOutputStream os;
	
	/**
	 * A compressed map of all the chunks to be written to file.
	 */
	protected final Map<ChunkCoordinate, byte[]> chunks = new HashMap<ChunkCoordinate, byte[]>();
	
	/**
	 * The region file's coordinates.
	 */
	protected final ChunkCoordinate offset;
	
	/**
	 * Create a new WorldOutputStream.
	 * @param os The base output stream to write to.
	 * @param offset The region's coordinates.
	 */
	public WorldOutputStream(OutputStream os, ChunkCoordinate offset) {
		this.os = new DataOutputStream(os);
		this.offset = offset;
	}
	
	/**
	 * Write a single chunk to the output stream.
	 * @param coord Coordinates in region file to write to.
	 * @param chunk Chunk NBT to write.
	 * @param name Name of NBT tag to write.
	 * @throws IOException If an IO exception occurs.
	 */
	public void write(ChunkCoordinate coord, NBTTag chunk, String name) throws IOException {
		// Make sure coordinates are in file.
		if (coord.x() < 0 || coord.x() >= SIZEX
				|| coord.z() < 0 || coord.z() >= SIZEZ) {
			throw new IllegalArgumentException("Chunk " + coord +" is outside of the range of the region file.");
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		NBTOutputStream nbtos = new NBTOutputStream(new DeflaterOutputStream(bos), false);
	
		nbtos.writeTag(chunk, name);
		nbtos.close();
		
		chunks.put(coord, bos.toByteArray());
	}
	
	/**
	 * Write a single chunk to the output stream.
	 * @param coord Coordinates in region file to write to.
	 * @param chunk Chunk NBT to write.
	 * @throws IOException If an IO exception occurs.
	 */
	public void write(ChunkCoordinate coord, NBTTag chunk) throws IOException {
		write(coord, chunk, "root");
	}
	
	public void flush() throws IOException {
		writeHeader();
		
		// Chunks must be arranged in certain order.
		List<ChunkCoordinate> chunkOrder = new ArrayList<ChunkCoordinate>();
		for (ChunkCoordinate c : chunks.keySet()) {
			chunkOrder.add(c);
		}
		Collections.sort(chunkOrder);
		
		// Write all chunks.
		for (ChunkCoordinate c : chunkOrder) {
			writeChunk(chunks.get(c));
		}
		
		os.flush();
	}
	
	/**
	 * Write the header of the region file.
	 * @throws IOException If an IO exception occurs.
	 */
	private void writeHeader() throws IOException {
		// Write chunk locations.
		int head = 2; // A virtual head for determining chunk location, in 4 KiB sectors.
		for (int z = 0; z < SIZEZ; z++) {
			for (int x = 0; x < SIZEX; x++) {
				ChunkCoordinate coord = new ChunkCoordinate(x, z);

				if (chunks.containsKey(coord)) {
					// Convert bytes to 4KiB sectors.
					byte length = (byte) (Math.ceil((double) (chunks.get(coord).length + 5) / 4096));
					int offsetLength = (head << 8) | length & 15;

					os.writeInt(offsetLength);
					
					head += length;
				} else {
					// If the chunk is non-existant, fill with 0s as specified in the format.
					os.writeInt(0);
				}
			}
		}
		
		// Write current time into timestamps table.
		int time = (int) java.time.Instant.now().toEpochMilli() / 100;
		byte[] timestamps = new byte[4096];
		for (int i = 0; i < timestamps.length; i++) {
			timestamps[i] = (byte) time;
		}
		os.write(timestamps);
	}
	
	/**
	 * Write a single chunk to file.
	 * @param data Compressed chunk data.
	 * @throws IOException If an IO exception occurs.
	 */
	private void writeChunk(byte[] data) throws IOException {
		// Write length.
		int length = data.length+1; // Length includes compression type byte (don't ask me why).
		os.writeInt(length);
		
		// Write compression type.
		os.write((byte) 2);
		
		// Write data.
		os.write(data);
		
		// Skip to the end of the sector.
		int sectors = (int) Math.ceil((double) (data.length + 5) / 4096);
		byte[] padding = new byte[sectors * 4096 - (length + 4)];
		os.write(padding);
	}

	@Override
	public void close() throws IOException {
		flush();
		os.close();
	}
	
}