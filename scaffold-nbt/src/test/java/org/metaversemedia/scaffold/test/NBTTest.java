package org.metaversemedia.scaffold.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.scaffoldeditor.nbt.Constants;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.io.ChunkParser;
import org.scaffoldeditor.nbt.io.WorldInputStream;
import org.scaffoldeditor.nbt.io.WorldOutputStream;
import mryurihi.tbnbt.tag.NBTTagCompound;

public class NBTTest {

	@Test
	public void test() {
		try {
			String INPUT_FILE = "/Users/h205p1/Documents/Scaffold-Workspace/testProject/game/saves/TestWorld/region/r.0.0.mca";
			String OUTPUT_FILE = "/Users/h205p1/Documents/Scaffold-Workspace/testProject/game/saves/world/region/r.0.0.mca";
			
			Chunk chunk = new Chunk();
			chunk.setBlock(0, 0, 0, new Block("minecraft:stone"));
			chunk.setBlock(1, 0, 0, new Block("minecraft:dirt"));
			
			ChunkParser parser = new ChunkParser(Constants.DEFAULT_DATA_VERSION);
			WorldOutputStream wos = new WorldOutputStream(new FileOutputStream(OUTPUT_FILE), new ChunkCoordinate(0,0));
			wos.write(new ChunkCoordinate(0,0), parser.writeNBT(chunk, 0, 0));
			System.out.println("Wrote world.");
			wos.close();

		} catch (Exception e) {
			System.out.println("THERE WAS AN ERROR!");
			e.printStackTrace();
		}
	}
	
//	// @Test
//	public void test() {
//		Chunk chunk = new Chunk();
//		chunk.setBlock(0, 0, 0, new Block("minecraft:stone"));
//		chunk.setBlock(0, 1, 0, new Block);
//		
//	}

}
