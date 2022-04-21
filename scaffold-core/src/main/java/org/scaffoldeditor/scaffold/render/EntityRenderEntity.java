package org.scaffoldeditor.scaffold.render;

import org.scaffoldeditor.nbt.util.MCEntity;

/**
 * Represents an entity that can be simply rendered as a Minecraft entity.
 * Supports both vanilla and modded entities.
 * 
 * @author Igrium
 */
public interface EntityRenderEntity extends PositionalRenderEntity {
    
    /**
     * Get the Minecraft entity being rendered. Note: updating this instance may not
     * update the representation in the world.
     * 
     * @return Minecraft entity.
     */
    MCEntity getMCEntity();

    /**
     * Set the Minecraft entity being rendered. Note: updating this instance may not
     * update the representation in the world.
     * 
     * @param entity Minecraft entity to render.
     */
    void setMCEntity(MCEntity entity);

    float getPitch();
    void setPitch(float pitch);

    float getYaw();
    void setYaw(float yaw);
}
