package org.scaffoldeditor.scaffold.render;

import org.joml.Vector3dc;

/**
 * Tells the editor to render a dynamic visualization of a brush. Mostly used
 * for tool brushes and other brush elements that don't have a physical
 * representation in the game.
 * 
 * @author Igrium
 */
public interface BrushRenderEntity extends RenderEntity {
    /**
     * Get the start position of the brush.
     * @return Start position.
     */
    Vector3dc getStartPos();

    /**
     * Set the start position of the brush.
     * @param startPosition Start position.
     */
    void setStartPos(Vector3dc startPos);

    /**
     * Get the end position of the brush.
     * @return End position.
     */
    Vector3dc getEndPos();

    /**
     * Set the end position of the brush. Must be greater than the start position.
     * @param endPos End position.
     */
    void setEndPos(Vector3dc endPos);

    /**
     * Get the texture of this brush.
     * @return Texture identifier.
     */
    String getTexture();

    /**
     * Set the texture of this brush.
     * @param texture Texture identifier.
     */
    void setTexture(String texture);
}
