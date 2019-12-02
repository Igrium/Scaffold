package org.metaversemedia.scaffold.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.io.WorldInputStream;
import org.scaffoldeditor.nbt.io.WorldOutputStream;

import mryurihi.tbnbt.tag.NBTTag;
import mryurihi.tbnbt.tag.NBTTagCompound;
import mryurihi.tbnbt.tag.NBTTagInt;
import mryurihi.tbnbt.tag.NBTTagString;

public class NBTTest {

	@Test
	public void test() {
		try {
			NBTTagCompound testTag = new NBTTagCompound(new HashMap<String, NBTTag>());
			testTag.put("testValue1", new NBTTagString("Test"));
			testTag.put("testValue2", new NBTTagInt(5));
			
			WorldOutputStream wos = new WorldOutputStream(new FileOutputStream("/Users/h205p1/Documents/Scaffold-Workspace/testProject/test.mca"));
			Map<ChunkCoordinate, NBTTagCompound> chunkCoordinates = new HashMap<ChunkCoordinate, NBTTagCompound>();
			chunkCoordinates.put(new ChunkCoordinate(0,0), testTag);
			wos.write(chunkCoordinates, 0, 0);
			wos.close();
			
			WorldInputStream wis = new WorldInputStream(new FileInputStream("/Users/h205p1/Documents/Scaffold-Workspace/testProject/test.mca"));
			NBTTagCompound testTag2 = wis.readChunkNBT().nbt;
			wis.close();
			
			System.out.println(testTag2);
			

		} catch (Exception e) {
			System.out.println("THERE WAS AN ERROR!");
			e.printStackTrace();
		}
	}

}
