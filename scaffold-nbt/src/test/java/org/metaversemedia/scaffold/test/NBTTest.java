package org.metaversemedia.scaffold.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.scaffoldeditor.nbt.NBTStrings;
import org.scaffoldeditor.nbt.io.WorldInputStream;
import org.scaffoldeditor.nbt.io.WorldInputStream.ChunkLocation;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.FloatTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;

public class NBTTest {

	@Test
	public void test() {
		try {
			int count = 0;
			WorldInputStream is = new WorldInputStream(new FileInputStream("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\game\\saves\\Test World\\region\\r.0.0.mca"));
			for (ChunkLocation c : is.chunkLocations) {
//				System.out.println(c.sectorCount);
				if (c.sectorCount > 0) {
					count++;
				}
			}
//			System.out.println(count);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
