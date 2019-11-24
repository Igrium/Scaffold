package org.metaversemedia.scaffold.test;

import java.io.FileInputStream;
import org.junit.Test;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.io.ChunkParser;
import org.scaffoldeditor.nbt.io.WorldInputStream;
import org.scaffoldeditor.nbt.io.WorldInputStream.ChunkNBTInfo;

public class NBTTest {

	@Test
	public void test() {
		try {
			int count = 0;
			WorldInputStream is = new WorldInputStream(new FileInputStream("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\game\\saves\\Test World\\region\\r.0.0.mca"));
//			WorldInputStream is = new WorldInputStream(new FileInputStream("/Users/h205p1/Documents/Scaffold-Workspace/testProject/game/saves/TestWorld/region/r.0.0.mca"));
			
			ChunkNBTInfo chunkNBT = is.readChunkNBT();
			Chunk chunk = ChunkParser.fromNBT(chunkNBT.nbt.get("Level").getAsTagCompound());
			
			System.out.println(chunk.blockAt(0, 128, 0));
			is.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
