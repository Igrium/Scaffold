package org.scaffoldeditor.scaffold.render;

import org.joml.Quaterniondc;
import org.scaffoldeditor.nbt.block.BlockCollection;

/**
 * Renders a collection of blocks as a holographic projection in the world.
 * 
 * @author Igrium
 */
public interface BlockRenderEntity extends PositionalRenderEntity {
    Quaterniondc getRotation();
    void setRotation(Quaterniondc rotation);
    
    /**
     * Get the blocks being rendered.
     * @return Blocks.
     */
    BlockCollection getBlocks();

    /**
     * Set the blocks being rendered.
     * @param blocks
     */
    void setBlocks(BlockCollection blocks);

}
