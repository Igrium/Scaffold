package org.metaversemedia.scaffold.test;

import static org.junit.Assert.*;

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
		
		for (Chunk c : blockworld.chunks()) {
			System.out.println(c.palette());
		}
		assert(true);
	}

}
