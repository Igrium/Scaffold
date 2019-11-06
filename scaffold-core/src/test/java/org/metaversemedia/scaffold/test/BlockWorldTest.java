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
		
		System.out.println("Testing blockworld.");
		try {
			for (Block block : blockworld) {
				System.out.println(block);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}
}
