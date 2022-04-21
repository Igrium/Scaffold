package org.scaffoldeditor.scaffold.render;

/**
 * A reference to any kind of Minecraft object that renders into the viewport.
 */
public interface RenderEntity {
    /**
     * Clean up and remove this entity from the world.
     */
    public void kill();

    /**
     * Stop this render entity from rendering.
     */
    public void disable();

    /**
     * Restore this render entity to rendering.
     */
    public void enable();
}
