package org.metaversemedia.scaffold.test;

import java.io.File;
import java.io.FileInputStream;
import org.junit.Test;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.io.ChunkParser;
import org.scaffoldeditor.nbt.io.WorldInputStream;
import org.scaffoldeditor.nbt.io.WorldInputStream.ChunkNBTInfo;

public class NBTTest {

	@Test
	public void test() {
		try {
			int count = 0;
			BlockWorld world = BlockWorld.deserialize(new File("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\game\\saves\\Test World\\region"));
			System.out.println(world.blockAt(-1, 5, -1));
			System.out.println(world.blockAt(11, 5, 16));
			System.out.println(world.blockAt(0, 0, 0));
			
//			for (Block b : world) {
//				if (b != null && b.getName().matches("minecraft:air")) {
//					System.out.print("{"+b+"}");
//				}
//			}

		} catch (Exception e) {
			System.out.println("THERE WAS AN ERROR!");
			e.printStackTrace();
		}
	}

}
