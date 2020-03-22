package org.metaversemedia.scaffold.test;

import java.io.*;
import java.util.*;

import org.junit.Test;
import org.scaffoldeditor.nbt.Constants;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.io.ChunkParser;
import org.scaffoldeditor.nbt.io.WorldInputStream;
import org.scaffoldeditor.nbt.io.WorldOutputStream;
import com.github.mryurihi.tbnbt.tag.NBTTagCompound;

public class NBTTest {

	@Test
	public void readCopiedRegionFile() throws IOException {
		File dir = new File("tests");
		dir.mkdirs();

		WorldInputStream worldInputStream = new WorldInputStream(new FileInputStream(new File(dir, "copied_r.0.0.mca")));

		while (worldInputStream.hasNext()) {
			WorldInputStream.ChunkNBTInfo info = worldInputStream.readChunkNBT();
			System.out.println(info.nbt.toString());
		}

		worldInputStream.close();
	}

	@Test
	public void copyRegionFile() throws IOException {
		File dir = new File("tests");
		dir.mkdirs();

		WorldInputStream worldInputStream = new WorldInputStream(new FileInputStream(new File(dir, "r.0.0.mca")));
		List<WorldInputStream.ChunkNBTInfo> chunks = new ArrayList<>();

		while (worldInputStream.hasNext()) {
			chunks.add(worldInputStream.readChunkNBT());
		}

		worldInputStream.close();

		WorldOutputStream wos = new WorldOutputStream(new FileOutputStream(new File(dir, "copied_r.0.0.mca")), new ChunkCoordinate(0,0));

		for (WorldInputStream.ChunkNBTInfo info : chunks) {
			wos.write(new ChunkCoordinate(info.x, info.z), info.nbt);
		}

		wos.close();
	}

	@Test
	public void readCreatedRegionFile() throws IOException {
		File dir = new File("tests");
		dir.mkdirs();

		WorldInputStream worldInputStream = new WorldInputStream(new FileInputStream(new File(dir, "created_r.0.0.mca")));

		while (worldInputStream.hasNext()) {
			WorldInputStream.ChunkNBTInfo info = worldInputStream.readChunkNBT();
			System.out.println(info.nbt.toString());
		}

		worldInputStream.close();
	}

	@Test
	public void createRegionFile() throws IOException {
		Chunk chunk = new Chunk();
		chunk.setBlock(0, 0, 0, new Block("minecraft:stone"));
		chunk.setBlock(1, 0, 0, new Block("minecraft:dirt"));

		File dir = new File("tests");
		dir.mkdirs();

		ChunkParser parser = new ChunkParser(Constants.DEFAULT_DATA_VERSION);
		WorldOutputStream wos = new WorldOutputStream(new FileOutputStream(new File(dir, "created_r.0.0.mca")), new ChunkCoordinate(0,0));
		wos.write(new ChunkCoordinate(0,0), parser.writeNBT(chunk, 0, 0));
		wos.close();
	}

}
