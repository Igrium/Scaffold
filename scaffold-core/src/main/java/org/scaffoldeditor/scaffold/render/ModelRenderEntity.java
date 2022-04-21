package org.scaffoldeditor.scaffold.render;

import org.joml.Quaterniondc;

/**
 * Tells the editor to render an arbitrary blockmodel. <br>
 * <b>Note:</b> Does not have an equivalent representation in Vanilla. Should
 * only be used for editor-only visualizations.
 * 
 * @author Igrium
 */
public interface ModelRenderEntity extends PositionalRenderEntity {
    Quaterniondc getRotation();
    void setRotation(Quaterniondc rotation);

    /**
     * Get the model being rendered.
     * @return Model identifier.
     */
    String getModel();
    
    /**
     * Set the model being rendered.
     * @param model Model identifier.
     */
    void setModel(String model);
}
