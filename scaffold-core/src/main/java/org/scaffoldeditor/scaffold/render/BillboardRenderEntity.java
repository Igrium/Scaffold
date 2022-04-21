package org.scaffoldeditor.scaffold.render;

/**
 * Tells the editor to render a "billboard". A billboard is a 2d texture that
 * has a position in 3d space. It's rendered on a plane at that position, always
 * facing the camera. Particles are a Minecraft example of billboards.
 * 
 * @author Igrium
 */
public interface BillboardRenderEntity extends PositionalRenderEntity {

    /**
     * Get the texture of this billboard.
     * @return Texture identifier.
     */
    String getTexture();

    /**
     * Set the texture of this billboard.
     * @param tex Texture identifier.
     */
    void setTexture(String tex);

    float getScale();
    void setScale(float scale);
}
