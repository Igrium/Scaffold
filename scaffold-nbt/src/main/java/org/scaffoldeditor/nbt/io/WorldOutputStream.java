package org.scaffoldeditor.nbt.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;

import mryurihi.tbnbt.stream.NBTOutputStream;
import mryurihi.tbnbt.tag.NBTTag;
import mryurihi.tbnbt.tag.NBTTagCompound;

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
	protected final OutputStream os;
	
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
		this.os = os;
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
		if (coord.x() < offset.x()*SIZEX || coord.x() >= (offset.x()+1)*SIZEX
				|| coord.z() < offset.z()*SIZEZ || coord.z() >= (offset.z()+1)*SIZEZ) {
			throw new IllegalArgumentException("Chunk is outside of the range of the region file.");
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		NBTOutputStream nbtos = new NBTOutputStream(new DeflaterOutputStream(bos), false);
	
		nbtos.writeTag(chunk, name);
		nbtos.close();
		
		chunks.put(coord, bos.toByteArray());
	}
	
	@Override
	public void close() throws IOException {
		
	}
	
}