package org.metaversemedia.scaffold.test;

import java.io.File;

import org.junit.Test;
import org.scaffoldeditor.nbt.Constants;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.schematic.Structure;

public class NBTTest {

	@Test
	public void test() {
		try {
//			Structure structure = Structure.fromFile(new File("/Users/h205p1/Documents/Scaffold-Workspace/testProject/house.nbt"));
			Structure structure = Structure.fromFile(new File("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\schematics\\house.nbt"));
			
			BlockWorld world = new BlockWorld();
			world.addBlockCollection(structure, 8, 10, 8);
			
//			world.writeRegionFile(new File("/Users/h205p1/Documents/Scaffold-Workspace/testProject/game/saves/world/region/r.0.0.mca"), 0, 0);
			world.writeRegionFile(new File("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\r.0.0.mca"), 0, 0,
					Constants.DEFAULT_DATA_VERSION);

		} catch (Exception e) {
			System.out.println("THERE WAS AN ERROR!");
			e.printStackTrace();
		}
	}

}
