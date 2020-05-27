package org.scaffoldeditor.editor.editor3d.block;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
}
