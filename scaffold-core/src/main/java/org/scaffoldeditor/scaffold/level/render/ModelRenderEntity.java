package org.scaffoldeditor.scaffold.level.render;

import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.level.entity.Entity;

/**
 * Tells the editor to render an arbitrary blockmodel. <br>
 * <b>Note:</b> Does not have an equivalent representation in Vanilla. Should
 * only be used for editor-only visualizations.
 * 
 * @author Igrium
 */
public class ModelRenderEntity extends RenderEntity {
	
	private String model;
	
	/**
	 * Create a render entity that renders an arbitry blockmodel.
	 * 
	 * @param entity     Owning Scaffold entity.
	 * @param position   Position of the render entity.
	 * @param identifier The unique identifier of this render entity, used to keep
	 *                   track of it in relation to its editor entity. Only one
	 *                   instance of this string may exist per Scaffold entity.
	 *                   Different Scaffold entities may share identifiers.
	 * @param model      Identifier of the blockstate from the resourcepack to use.
	 *                   Format: <code>[namespace]:[blockstate]#[variant]</code>
	 *                   Example:
	 *                   <code>minecraft:cobblestone_stairs#inventory</code>
	 */
	public ModelRenderEntity(Entity entity, Vector3dc position, Vector3dc rotation, String identifier, String model) {
		super(entity, position, rotation, identifier);
		this.model = model;
	}

	public String getModel() {
		return model;
	}
	
	@Override
	public String toString() {
		return super.toString() + " model: "+model;
	}
}
