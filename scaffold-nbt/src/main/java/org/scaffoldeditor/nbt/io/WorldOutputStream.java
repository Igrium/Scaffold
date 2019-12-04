package org.scaffoldeditor.nbt.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;

import mryurihi.tbnbt.stream.NBTInputStream;
import mryurihi.tbnbt.stream.NBTOutputStream;
import mryurihi.tbnbt.tag.NBTTagCompound;

public class WorldOutputStream implements Closeable {
	
	private final OutputStream os;
	
	public WorldOutputStream(OutputStream os) {
		this.os = os;
	}
	
	/**
	 * Write a collection of chunks to the region file denoted by the outputstream.
	 * @param chunks A map mapping local chunk coordinates (within the region file) to NBT compound maps representing the chunks (see ChunkParser).
	 * @param offsetX The X offset (in chunk coords) of the file.
	 * @param offsetZ The Z offset (in chink coords) of the file.
	 * @throws IOException If an IO Exception occurs.
	 */
	public void write(Map<ChunkCoordinate, NBTTagCompound> chunks, int offsetX, int offsetZ) throws IOException {
		System.out.println("Writing region file...");
		// Remove all chunks out of range.
		for (ChunkCoordinate coord : chunks.keySet()) {
			if (coord.x() < 0 || coord.x() > 32 || coord.z() < 0 || coord.z() > 32){
				chunks.remove(coord);
				System.out.println("Chunk at "+coord.x()+", "+coord.z()+" is out of range of the region file! Removing.");
			}
		}
		
		// Compress all chunks.
		System.out.println("Compressing chunks...");
		Map<ChunkCoordinate, byte[]> compressed = new HashMap<ChunkCoordinate, byte[]>();
		for (ChunkCoordinate coord : chunks.keySet()) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			NBTOutputStream nbtos = new NBTOutputStream(new DeflaterOutputStream(bos), false);
//			NBTOutputStream nbtos = new NBTOutputStream(bos);

			nbtos.writeTag(chunks.get(coord), "root");
			nbtos.close();
			
			compressed.put(coord, bos.toByteArray());
			System.out.println(bos.toByteArray().length);
		}
		
		// Chunks must be in a particular order: in coordiate order with the Z coordinate dominant.
		List<ChunkCoordinate> coords = new ArrayList<ChunkCoordinate>(chunks.keySet());
		Collections.sort(coords);
		
		// Write location metadata.
		int head = 2; // A virtual head for determining chunk location, in 4 KiB sectors.
		Map<ChunkCoordinate, Integer> locationOffsets = new HashMap<ChunkCoordinate, Integer>();
		for (int z = 0; z < 32; z++) {
			for (int x = 0; x < 32; x++) {
				ChunkCoordinate coord = new ChunkCoordinate(x, z);
				if (compressed.containsKey(coord)) {
					byte[] offset = intToByteArray(head);
					os.write(offset);
					
					byte length = (byte) (Math.ceil((double) (compressed.get(coord).length+5)/4096)); // Convert bytes to 4KiB sectors.
					os.write(length);
					
					System.out.println(head+", "+length); // TESTING ONLY
					locationOffsets.put(coord, head);
					
					System.out.println("head: "+head);

					head += length;
					
				} else {
					os.write(new byte[] {0,0,0,0}); // If the chunk is non-existant, fill with 0s as specified in the format.
				}
			} 
		}
		
		// Write current time into timestamps table.
		int time = (int) java.time.Instant.now().toEpochMilli()/100;
		byte[] timestamps = new byte[4096];
		for (int i = 0; i < timestamps.length; i++) {
			timestamps[i] = (byte) time; 
		}
		os.write(timestamps);
		
		// Write chunks
		for (ChunkCoordinate coord : coords) {
			writeChunk(compressed.get(coord));
		}
		
		
		
	}
	
	/**
	 * Write a single chunk.
	 * @param data Compressed chunk data.
	 * @throws IOException If an IO exception occurs.
	 */
	private void writeChunk(byte[] data) throws IOException {
		// Write length;
		byte[] length = new byte[4];
		int lengthNum = data.length+1;
		
		length[0] = (byte) (lengthNum >> 24);
		length[1] = (byte) (lengthNum >> 16);
		length[2] = (byte) (lengthNum >> 8);
		length[3] = (byte) (lengthNum /*>> 0*/);
		
		System.out.println("Length: "+lengthNum);
		os.write(length);

				
		// Write compression type.
		os.write(2);
		
		// Write data.
		os.write(data);
		
		// Skip to the end of the segment.
		int sectors = (int) Math.ceil((double) data.length/4096);
		byte[] padding = new byte[sectors*4096 - (lengthNum + 4)];
		os.write(padding);
		System.out.println("Padding: "+padding.length);
		
		// Decompress for debug
		NBTInputStream nis = new NBTInputStream(new InflaterInputStream(new ByteArrayInputStream(data)), false);
		System.out.println("Uncompressed data (after compression and decompression): "+nis.readTag());

	}

	@Override
	public void close() throws IOException {
		os.close();
	}
	
	/**
	 * Convert an integer to a big endian 3 long byte array.
	 */
	private byte[] intToByteArray(int in) {
		return new byte[] {(byte) ((in >> 16) & 0xFF), (byte) ((in >> 8) & 0xFF), (byte) (in & 0xFF)};
	}

}
