package org.scaffoldeditor.scaffold.render;

import org.joml.Vector3dc;
import org.joml.Vector4fc;

public interface LineRenderEntity extends RenderEntity {
     /**
     * Get the start position of the line.
     * @return Start position.
     */
    Vector3dc getStartPos();

    /**
     * Set the start position of the line.
     * @param startPosition Start position.
     */
    void setStartPos(Vector3dc startPos);

    /**
     * Get the end position of the line.
     * @return End position.
     */
    Vector3dc getEndPos();

    /**
     * Set the end position of the line.
     * @param endPos End position.
     */
    void setEndPos(Vector3dc endPos);

    /**
     * Get the color of the line.
     * @return A 4 dimensional vector, where x = red, y = green, z = blue, and w = alpha
     */
    Vector4fc getColor();

    /**
     * Set the color of the line.
     * @param color A 4 dimensional vector, where x = red, y = green, z = blue, and w = alpha
     */
    void setColor(Vector4fc color);
}
