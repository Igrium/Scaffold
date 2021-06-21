package org.scaffoldeditor.scaffold.level.render;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.entity.Entity;

/**
 * Tells the editor to render a dynamic visualization of a brush. Mostly used
 * for tool brushes and other brush elements that don't have a physical
 * representation in the game.
 * 
 * @author Igrium
 */
public class BrushRenderEntity extends RenderEntity {
	
	private Vector3f endPos;
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
	public BrushRenderEntity(Entity entity, Vector3f position, Vector3f endPos, String texture, String identifier) {
		super(entity, position, identifier);
		this.endPos = endPos;
		this.texture = texture;
	}
	
	public String getTexture() {
		return texture;
	}
	
	public Vector3f getEndPos() {
		return endPos;
	}

}
