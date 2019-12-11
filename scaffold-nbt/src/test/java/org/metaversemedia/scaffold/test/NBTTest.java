package org.metaversemedia.scaffold.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.io.WorldInputStream;
import org.scaffoldeditor.nbt.io.WorldOutputStream;
import mryurihi.tbnbt.tag.NBTTagCompound;

public class NBTTest {

	@Test
	public void test() {
		try {
			String INPUT_FILE = "/Users/h205p1/Documents/Scaffold-Workspace/testProject/game/saves/TestWorld/region/r.0.0.mca";
			String OUTPUT_FILE = "/Users/h205p1/Documents/Scaffold-Workspace/testProject/game/saves/world/region/r.0.0.mca";
			
			WorldInputStream wis = new WorldInputStream(new FileInputStream(INPUT_FILE));
			Map<ChunkCoordinate, NBTTagCompound> chunks = new HashMap<ChunkCoordinate, NBTTagCompound>();
			while (wis.hasNext()) {
				NBTTagCompound root = wis.readChunkNBT().nbt;
				NBTTagCompound chunk = root.get("Level").getAsTagCompound();
				chunks.put(new ChunkCoordinate(
						chunk.get("xPos").getAsTagInt().getValue(),
						chunk.get("zPos").getAsTagInt().getValue()), chunk);
			}
			wis.close();
			
			System.out.println("Writing region file...");
			WorldOutputStream wos = new WorldOutputStream(new FileOutputStream(OUTPUT_FILE), new ChunkCoordinate(0,0));
			for (ChunkCoordinate c : chunks.keySet()) {
				wos.write(c, chunks.get(c));
			}
			
			wos.close();
			
			// Re-Read for testing
			WorldInputStream wis2 = new WorldInputStream(new FileInputStream(OUTPUT_FILE));
			int i = 0;
			while (wis2.hasNext()) {
				i++;
				System.out.println("Reading chunk "+i);
				System.out.println(wis2.readChunkNBT().nbt.getTagType());
			}
			wis2.close();

		} catch (Exception e) {
			System.out.println("THERE WAS AN ERROR!");
			e.printStackTrace();
		}
	}

}
