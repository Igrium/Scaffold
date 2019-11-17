package org.metaversemedia.scaffold.test;

import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Test;
import org.scaffoldeditor.nbt.NBTStrings;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.io.WorldInputStream;
import org.scaffoldeditor.nbt.io.WorldInputStream.ChunkLocation;
import org.scaffoldeditor.nbt.io.WorldInputStream.ChunkNBTInfo;

public class NBTTest {

	@Test
	public void test() {
		try {
			int count = 0;
			WorldInputStream is = new WorldInputStream(new FileInputStream("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\game\\saves\\Test World\\region\\r.0.0.mca"));
//			WorldInputStream is = new WorldInputStream(new FileInputStream("/Users/h205p1/Documents/Scaffold-Workspace/testProject/game/saves/TestWorld/region/r.0.0.mca"));
			
			ChunkNBTInfo chunkNBT = is.readChunkNBT();
			Chunk chunk = Chunk.fromNBT(chunkNBT.nbt.get("Level").getAsTagCompound());
			
			is.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
