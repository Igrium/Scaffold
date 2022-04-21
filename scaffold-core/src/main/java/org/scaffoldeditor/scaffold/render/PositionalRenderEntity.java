package org.scaffoldeditor.scaffold.render;

import org.joml.Vector3dc;

/**
 * A render entity that has a set position in the world.
 */
public interface PositionalRenderEntity extends RenderEntity {

    /**
     * Get the position of this render entity.
     * @return World position.
     */
    Vector3dc getPosition();

    /**
     * Set the position of this render entity.
     * @param pos World position.
     */
    void setPosition(Vector3dc pos);

}
