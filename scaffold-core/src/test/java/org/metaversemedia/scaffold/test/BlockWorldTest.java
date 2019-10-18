package org.metaversemedia.scaffold.test;

import org.junit.Test;
import org.scaffoldeditor.nbt.Block;
import org.scaffoldeditor.scaffold.level.BlockWorld;

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
//s
//		System.out.println(blockworld.blockAt(4, 23, 55));
//		System.out.println(blockworld.blockAt(20, 4, 7));
//		
//		for (Chunk c : blockworld.chunks()) {
//			System.out.println(c);	
//		}
		

	}
}
