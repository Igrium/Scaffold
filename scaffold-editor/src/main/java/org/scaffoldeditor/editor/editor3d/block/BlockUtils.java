package org.scaffoldeditor.editor.editor3d.block;

import org.scaffoldeditor.nbt.block.Block;

import com.rvandoosselaer.blocks.BlockIds;
import com.rvandoosselaer.blocks.BlockRegistry;
import com.rvandoosselaer.blocks.BlocksConfig;

/**
 * Utility class to handle conversion between Scaffold blocks and render blocks.
 * @author Sam54123
 *
 */
public final class BlockUtils {
	/**
	 * Convert a Scaffold block to a render block.
	 * @param sBlock Scaffold block.
	 * @return Render block.
	 */
	public static com.rvandoosselaer.blocks.Block scaffoldToRenderBlock(Block sBlock) {
		BlockRegistry blockRegistry = BlocksConfig.getInstance().getBlockRegistry();
		
//		return blockRegistry.get(sBlock.getName());
		return blockRegistry.get(BlockIds.COBBLESTONE);
	}
}
