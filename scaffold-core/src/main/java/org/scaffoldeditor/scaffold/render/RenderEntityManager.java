package org.scaffoldeditor.scaffold.render;

import java.util.Set;

/**
 * An interface between the Scaffold API and the Minecraft API allowing for
 * Scaffold objects to render things in the editor. The base class is but a
 * wrapper for an implementation that uses the Minecraft API.
 */
public abstract class RenderEntityManager<T extends RenderEntity> {
    private static RenderEntityManager<?> active;
    
    /**
     * Get the current render entity manager.
     * @return Current render entity manager.
     * @throws IllegalStateException If no render entity manager has been created.
     */
    public static RenderEntityManager<?> getInstance() throws IllegalStateException {
        if (active == null) {
            throw new IllegalStateException("No render entity manager has been created!");
        }
        return active;
    }

    /**
     * <p>Get the current render entity manager. Subsequent calls to <code>getInstance()</code> will return this.</p>
     * <p><i><b>Warning:</b> calling this without knowing what you're doing can break the entire editor!</i></p>
     * @param instance New instance.
     */
    public static void setInstance(RenderEntityManager<?> instance) {
        if (active != null) {
            active.clear();
        }
        active = instance;
    }

    /**
     * Remove a render entity from the render pool. This method is dangerous, as it
     * does not inform the render entity of this action! In general,
     * {@link RenderEntity#kill()} should be used instead!
     * 
     * @param rEnt The render entity to remove.
     * @return Was the entity found?
     */
    public abstract boolean remove(T rEnt);
    
    public abstract BillboardRenderEntity createBillboard();
    public abstract BlockRenderEntity createBlock();
    public abstract BrushRenderEntity createBrush();
    public abstract LineRenderEntity createLine();
    public abstract EntityRenderEntity createMC();
    public abstract ModelRenderEntity createModel();

    
    /**
     * Get all of the render entities in the render pool. Entities are automatically
     * added to this set when spawned.
     * 
     * @return A set of all render entities. Modifying this set may cause instability!
     */
    public abstract Set<T> getPool();
    
    /**
     * Clear the entire render pool.
     */
    public void clear() {
        for (RenderEntity ent : getPool()) {
            ent.kill();
        }
    }
}
