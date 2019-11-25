package org.metaversemedia.scaffold.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.junit.Test;
import org.scaffoldeditor.nbt.Constants;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.io.ChunkParser;
import org.scaffoldeditor.nbt.io.WorldInputStream;
import org.scaffoldeditor.nbt.io.WorldOutputStream;

import mryurihi.tbnbt.tag.NBTTag;
import mryurihi.tbnbt.tag.NBTTagCompound;
import mryurihi.tbnbt.tag.NBTTagInt;

public class NBTTest {

	@Test
	public void test() {
		try {
			BlockWorld world = BlockWorld.deserialize(new File("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\game\\saves\\Test World\\region"));
			System.out.println(world.blockAt(-1, 5, -1));
			System.out.println(world.blockAt(11, 5, 16));
			System.out.println(world.blockAt(0, 0, 0));
			
//			for (Block b : world) {
//				if (b != null && b.getName().matches("minecraft:air")) {
//					System.out.print("{"+b+"}");
//				}
//			}
			
			Chunk original = world.chunkAt(0, 0);
			System.out.println("Original: "+original.blockAt(1, 0, 0));
			
			NBTTagCompound chunkNBT = ChunkParser.writeNBT(world.chunkAt(0, 0));
			System.out.println(chunkNBT);
			
			NBTTagCompound region = new NBTTagCompound(new HashMap<String, NBTTag>());
			region.put("DataVersion", new NBTTagInt(Constants.DEFAULT_DATA_VERSION));
			region.put("Level", chunkNBT);
			
			Chunk chunk2 = world.chunkAt(0, 1);
			NBTTagCompound chunkNBT2 = ChunkParser.writeNBT(chunk2);
			
			NBTTagCompound region2 = new NBTTagCompound(new HashMap<String, NBTTag>());
			region2.put("DataVersion", new NBTTagInt(Constants.DEFAULT_DATA_VERSION));
			region2.put("Level", chunkNBT2);
			
			HashMap<ChunkCoordinate, NBTTagCompound> regionData = new HashMap<ChunkCoordinate, NBTTagCompound>();
			regionData.put(new ChunkCoordinate(0,0), region);
			regionData.put(new ChunkCoordinate(0,1), region2);
			
			
			WorldOutputStream out = new WorldOutputStream(new FileOutputStream("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\r.0.0.mca"));
			out.write(regionData, 0, 0);
			out.close();
			
			// Re-read the file for testing.
			System.out.println("--- Re-reading chunk ---");
			WorldInputStream in = new WorldInputStream(new FileInputStream("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\r.0.0.mca"));
			System.out.println(in.readChunkNBT());

		} catch (Exception e) {
			System.out.println("THERE WAS AN ERROR!");
			e.printStackTrace();
		}
	}

}
