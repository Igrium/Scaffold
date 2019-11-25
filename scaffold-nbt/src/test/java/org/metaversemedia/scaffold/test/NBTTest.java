package org.metaversemedia.scaffold.test;

import java.io.File;
import org.junit.Test;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.io.ChunkParser;

import mryurihi.tbnbt.tag.NBTTagCompound;

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
			
			Chunk original = world.chunkAt(0, 1);
			System.out.println("Original: "+original.blockAt(1, 0, 0));
			NBTTagCompound chunkNBT = ChunkParser.writeNBT(world.chunkAt(0, 0));
			System.out.println(chunkNBT);
			
			Chunk chunk = ChunkParser.parseNBT(chunkNBT);
			System.out.println("Written: "+chunk.blockAt(1, 0, 0));
			ChunkDebugger debugger = new ChunkDebugger();
			debugger.characters.put("minecraft:bedrock", '@');
			debugger.printLayer(0, original);

		} catch (Exception e) {
			System.out.println("THERE WAS AN ERROR!");
			e.printStackTrace();
		}
	}

}
