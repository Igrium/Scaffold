package org.metaversemedia.scaffold.test;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.Chunk;

public class ChunkDebugger {
	/**
	 * A map mapping block names to character representations for debugging.
	 */
	public final Map<String, Character> characters = new HashMap<String, Character>();
	
	public void printLayer(int layer, Chunk chunk) {
		System.out.print("\n");
		for (int z = 0; z < 16; z++) {
			for (int x = 0; x < 16; x++) {
				System.out.print(blockToChar(chunk.blockAt(x, layer, z)));
			}
			System.out.print("\n");
		}
	}
	
	private char blockToChar(Block block) {
		if (characters.containsKey(block.getName())) {
			return characters.get(block.getName());
		} else {
			return '*';
		}
	}
}
