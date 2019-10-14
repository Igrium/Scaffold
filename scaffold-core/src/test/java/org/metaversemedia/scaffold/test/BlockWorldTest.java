package org.metaversemedia.scaffold.test;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.metaversemedia.scaffold.level.BlockWorld;
import org.metaversemedia.scaffold.level.Chunk;
import org.metaversemedia.scaffold.nbt.Block;
import org.metaversemedia.scaffold.nbt.schematic.Structure;

import com.flowpowered.nbt.CompoundMap;

public class BlockWorldTest {

	@Test
	public void test() {
		BlockWorld blockworld = new BlockWorld();
		blockworld.setBlock(4, 23, 56, new Block("minecraft:wood", new CompoundMap()));
		blockworld.setBlock(4, 23, 55, new Block("minecraft:stone", new CompoundMap()));
		blockworld.setBlock(0,0,0, new Block("minecraft:iron_ore", new CompoundMap()));
		
//		try {
//			Structure house = Structure.fromFile(new File("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\schematics\\house.nbt"));
//			blockworld.addBlockCollection(house, 16, 0, 0);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		System.out.println(blockworld.blockAt(4, 23, 55));
		System.out.println(blockworld.blockAt(20, 4, 7));
		
		for (Chunk c : blockworld.chunks()) {
			System.out.println(c);	
		}
		assert(true);
	}

}