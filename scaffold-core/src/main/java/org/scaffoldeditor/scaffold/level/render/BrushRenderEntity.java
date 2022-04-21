package org.scaffoldeditor.scaffold.level.render;

import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.entity.Entity;

/**
 * Tells the editor to render a dynamic visualization of a brush. Mostly used
 * for tool brushes and other brush elements that don't have a physical
 * representation in the game.
 * 
 * @author Igrium
 */
@Deprecated
public class BrushRenderEntity extends RenderEntity {
	
	private Vector3dc endPos;
	private String texture;
	
	/**
	 * Create a brush render entity.
	 * 
	 * @param entity     Owning Scaffold entity.
	 * @param position   Start position of the brush.
	 * @param endPos     End position of the brush.
	 * @param texture    Texture to use on the brush.
	 *                   (<code>[namespace]:textures/[texture].png</code>)
	 * @param identifier The unique identifier of this render entity, used to keep
	 *                   track of it in relation to its editor entity. Only one
	 *                   instance of this string may exist per Scaffold entity.
	 *                   Different Scaffold entities may share identifiers.
	 */
	public BrushRenderEntity(Entity entity, Vector3dc position, Vector3dc endPos, String texture, String identifier) {
		super(entity, position, identifier);
		this.endPos = endPos;
		this.texture = texture;
	}
	
	public String getTexture() {
		return texture;
	}
	
	public Vector3dc getEndPos() {
		return endPos;
	}

}
