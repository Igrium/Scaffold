package org.scaffoldeditor.editor.editor3d.block;

import java.util.HashMap;
import java.util.Map;

import com.rvandoosselaer.blocks.Block;
import com.rvandoosselaer.blocks.TypeIds;

/**
 * Manages the loading and saving of JME Block objects.
 * <br>
 * Each blockmodel is a separate block.
 * @author Igrium
 */
public class BlockManager {
	
	private Map<String, Block> registry = new HashMap<String, Block>();
	
	
	/**
	 * Check if the registry already contains a block.
	 * @param key JME path to block's model file.
	 * @return Has the block?
	 */
	public boolean has(String key) {
		return registry.containsKey(key);
	}
	
	/**
	 * Get a block from the registry, or generate it if it doesn't exist.
	 * @param key JME path to block's model file.
	 * @return JME block.
	 */
	public Block get(String key) {
		Block block = registry.get(key);
		
		if (block == null) {
			return generateBlock(key);
		}
		
		return block;
	}
	
	/**
	 * Generate a block from a model file and add it to the registry.
	 * @param key JME path to block's model file.
	 * @return Generated block.
	 */
	protected Block generateBlock(String key) {
		Block block = new Block(key, key, TypeIds.COBBLESTONE, false, false, true);
		registry.put(key, block);
		
		return block;
	}
}
