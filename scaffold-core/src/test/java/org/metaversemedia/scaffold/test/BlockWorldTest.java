package org.metaversemedia.scaffold.test;

import org.junit.Test;
import org.metaversemedia.scaffold.level.BlockWorld;
import org.metaversemedia.scaffold.level.Chunk;
import org.metaversemedia.scaffold.nbt.Block;

import com.flowpowered.nbt.CompoundMap;

public class BlockWorldTest {

	@Test
	public void test() {
		BlockWorld blockworld = new BlockWorld();
		blockworld.setBlock(4, 23, 56, new Block("minecraft:wood", new CompoundMap()));
		blockworld.setBlock(4, 23, 55, new Block("minecraft:stone", new CompoundMap()));
		blockworld.setBlock(0,0,0, new Block("minecraft:iron_ore", new CompoundMap()));

		System.out.println(blockworld.blockAt(4, 23, 55));
		System.out.println(blockworld.blockAt(0, 0, 0));
		
		for (Chunk c : blockworld.chunks()) {
			System.out.println(c);	
		}
		assert(true);
	}

}
