package org.metaversemedia.scaffold.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.scaffoldeditor.nbt.Constants;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.io.ChunkParser;
import org.scaffoldeditor.nbt.io.WorldInputStream;
import org.scaffoldeditor.nbt.io.WorldInputStream.ChunkNBTInfo;
import org.scaffoldeditor.nbt.io.WorldOutputStream;
import mryurihi.tbnbt.tag.NBTTagCompound;

public class NBTTest {

	@Test
	public void test() {
		try {
			String INPUT_FILE = "C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\game\\saves\\world\\region\\r.0.-1.mca";
			String OUTPUT_FILE = "C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\game\\saves\\world\\region\\r.0.0.mca";
			
			Chunk chunk = new Chunk();
			chunk.setBlock(0, 0, 0, new Block("minecraft:stone"));
			chunk.setBlock(1, 0, 0, new Block("minecraft:dirt"));
			
			ChunkParser parser = new ChunkParser(Constants.DEFAULT_DATA_VERSION);
			WorldOutputStream wos = new WorldOutputStream(new FileOutputStream(OUTPUT_FILE), new ChunkCoordinate(0,0));
			wos.write(new ChunkCoordinate(0,0), parser.writeNBT(chunk, 0, 0));
			System.out.println("Wrote world.");
			wos.close();
			
			System.out.println("Reading world...");
			WorldInputStream wis = new WorldInputStream(new FileInputStream(OUTPUT_FILE));
			NBTTagCompound ChunkNBT = wis.readChunkNBT().nbt;
			wis.close();
			
			Chunk chunk2 = ChunkParser.parseNBT(ChunkNBT);
			System.out.println("Block at 0,0 is " + chunk2.blockAt(2, 0, 0));

		} catch (Exception e) {
			System.out.println("THERE WAS AN ERROR!");
			e.printStackTrace();
		}
	}
	
	@Test
	public void test2() {
		long[] longs = new long[] {12903849504923049L, 213872305987273L};
		
		int[] parsed = ChunkParser.readBlockStates(longs);
		
		long[] written = ChunkParser.writeBlockStates(parsed);
		System.out.println("Original: "+Arrays.toString(longs));
		System.out.println("Written: "+Arrays.toString(written));
		
		int[] parsed2 = ChunkParser.readBlockStates(written);
		
		assert(Arrays.equals(parsed, parsed2));
		
	}

}
