package org.scaffoldeditor.scaffold.render;

import java.util.HashSet;
import java.util.Set;

import org.scaffoldeditor.scaffold.entity.Entity;

/**
 * An interface between the Scaffold API and the Minecraft API allowing for
 * Scaffold objects to render things in the editor. The base class is but a
 * wrapper for an implementation that uses the Minecraft API.
 */
public abstract class RenderEntityManager<T extends RenderEntity> {
    private static RenderEntityManager<?> active;
    
    /**
     * Get the current render entity manager.
     * @return Current render entity manager, or <code>null</code> if none has been created.
     */
    public static RenderEntityManager<?> getInstance(){
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
     * Tell the render system that a certian entity "owns" a render entity. This
     * allows it to be properly hidden and unhidden with the entity.
     * 
     * @param rEntity Target render entity.
     * @param owner   Owner to assign.
     * @throws IllegalArgumentException If this render entity does not belong to
     *                                  this render entity manager.
     */
    public abstract void assignOwner(RenderEntity rEntity, Entity owner) throws IllegalArgumentException;

    public BillboardRenderEntity createBillboard(Entity owner) {
        BillboardRenderEntity ent = createBillboard();
        assignOwner(ent, owner);
        return ent;
    }

    public BlockRenderEntity createBlock(Entity owner) {
        BlockRenderEntity ent = createBlock();
        assignOwner(ent, owner);
        return ent;
    }

    public BrushRenderEntity createBrush(Entity owner) {
        BrushRenderEntity ent = createBrush();
        assignOwner(ent, owner);
        return ent;
    }

    public LineRenderEntity createLine(Entity owner) {
        LineRenderEntity ent = createLine();
        assignOwner(ent, owner);
        return ent;
    }

    public EntityRenderEntity createMC(Entity owner) {
        EntityRenderEntity ent = createMC();
        assignOwner(ent, owner);
        return ent;
    }

    public ModelRenderEntity createModel(Entity owner) {
        ModelRenderEntity ent = createModel();
        assignOwner(ent, owner);
        return ent;
    }
    
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
        // Duplicate the pool so we don't get a concurrent modification exception.
        Set<T> dupe = new HashSet<>();
        dupe.addAll(getPool());
        for (RenderEntity ent : dupe) {
            ent.kill();
        }
    }
}
