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
     * Check whether this render entity has been killed.
     * @return Is this render entity alive?
     */
    public boolean isAlive();

    /**
     * Utility function to make sure a render entity reference is not null and the
     * entity is alive.
     * 
     * @param ent Entity reference to check.
     * @return Is this entity valid?
     */
    public static boolean isValid(RenderEntity ent) {
        return (ent != null && ent.isAlive());
    }
}
