package org.metaversemedia.scaffold.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import mryurihi.tbnbt.tag.NBTTagInt;
import mryurihi.tbnbt.tag.NBTTag;

public class NBTTest {

	@Test
	public void test() {
		try {
			String INPUT_FILE = "C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\game\\saves\\world\\region\\r.0.1.mca";
			String OUTPUT_FILE = "C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\game\\saves\\world\\region\\r.0.0.mca";
			
			copyFile(INPUT_FILE, OUTPUT_FILE);
			
			// Re-read for testing
			WorldInputStream wis = new WorldInputStream(new FileInputStream(OUTPUT_FILE));
			System.out.println("Re-read: "+wis.readChunkNBT().nbt);
			wis.close();

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
	
	private void copyFile(String input, String output) throws FileNotFoundException, IOException {
//		WorldInputStream wis = new WorldInputStream(new FileInputStream(input));
		WorldOutputStream wos = new WorldOutputStream(new FileOutputStream(output), new ChunkCoordinate(0,0));
		
		Chunk chunk = new Chunk();
		chunk.setBlock(0, 0, 0, new Block("minecraft:stone"));
		ChunkParser parser = new ChunkParser(Constants.DEFAULT_DATA_VERSION);
		
		NBTTagCompound testNBT = parser.writeNBT(chunk, 0, 0);
		wos.write(new ChunkCoordinate(0,0), testNBT);
//		while (wis.hasNext()) {
//			ChunkNBTInfo nbt = wis.readChunkNBT();
//			System.out.println("Data Version: "+nbt.nbt.get("DataVersion"));
//			nbt.nbt.put("DataVersion", new NBTTagInt(32));
//			NBTTagCompound demoNBT = new NBTTagCompound(new HashMap<String, NBTTag>());
////			demoNBT.put("DataVersion", new NBTTagInt(33));
////			NBTTagCompound level = 
//			
//			
//		}
		
//		wis.close();
		wos.close();
	}

}
