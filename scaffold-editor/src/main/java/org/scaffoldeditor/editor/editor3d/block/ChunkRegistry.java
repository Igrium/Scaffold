package org.scaffoldeditor.editor.editor3d.block;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.Chunk;

import com.simsilica.mathd.Vec3i;


/**
 * Responsble for keeping track of the corrilation between core chunks and render chunks.
 * @author Sam54123
 *
 */
public class ChunkRegistry {
	protected static Map<Chunk, com.rvandoosselaer.blocks.Chunk> registry = new HashMap<Chunk, com.rvandoosselaer.blocks.Chunk>();
	
	/**
	 * Blocks with these names are not included in the render chunk.
	 */
	protected static String[] skipBlocks = {"minecraft:air"};
	
	/**
	 * Register a new chunk.
	 * @param chunk Chunk to register.
	 * @param location Location of chunk in the world.
	 * @return The generated render chunk.
	 */
	public static com.rvandoosselaer.blocks.Chunk registerChunk(Chunk chunk, Vec3i location) {
		if (registry.containsKey(chunk)) {
			return registry.get(chunk);
		}
		
		com.rvandoosselaer.blocks.Chunk renderChunk = com.rvandoosselaer.blocks.Chunk.createAt(location);
		registry.put(chunk, renderChunk);
		return renderChunk;
	}
	
	/**
	 * Get a render chunk from the registry.
	 * @param chunk Chunk to get the render chunk of.
	 * @return The render chunk, or null if the chunk isn't in the registry.
	 */
	public static com.rvandoosselaer.blocks.Chunk get(Chunk chunk) {
		return registry.get(chunk);
	}
	
	/**
	 * Remove a chunk from the registry.
	 * @param chunk Chunk to remove.
	 * @return The render chunk this chunk used to map to, or null if it wasn't in the registry.
	 */
	public static com.rvandoosselaer.blocks.Chunk unregisterChunk(Chunk chunk) {
		return registry.remove(chunk);
	}
	
	/**
	 * Remove everything from the chunk registry.
	 */
	public static void clean() {
		for (Chunk c : chunks()) {
			get(c).cleanup();
			registry.remove(c);
		}
	}
	
	/**
	 * Returns the set of registered chunks.
	 * @return The set of registered chunks.
	 */
	public static Set<Chunk> chunks() {
		return registry.keySet();
	}
	
	/**
	 * Returns the collection of registered render chunks.
	 * @return The collection of registered render chunks.
	 */
	public static Collection<com.rvandoosselaer.blocks.Chunk> renderChunks() {
		return registry.values();
	}
	
	/**
	 * Refresh the render chunk of a chunk.
	 * @param chunk Chunk to refresh the render chunk of.
	 */
	public static void refreshChunk(Chunk chunk) {
		com.rvandoosselaer.blocks.Chunk renderChunk = get(chunk);
		
		List<String> skipBlocksList = Arrays.asList(skipBlocks);
		
		for (int y = 0; y < Chunk.HEIGHT; y++) {
			for (int z = 0; z < Chunk.LENGTH; z++) {
				for (int x = 0; x < Chunk.WIDTH; x++) {
					renderChunk.removeBlock(x, y, z);
					
					Block sBlock = chunk.blockAt(x, y, z);
					if (sBlock != null && skipBlocksList.contains(sBlock.getName())) {
						renderChunk.addBlock(x, y, z,
								BlockUtils.scaffoldToRenderBlock(chunk.blockAt(x, y, z)));
					}
				}
			}
		}
		
		renderChunk.update();
	}
}
