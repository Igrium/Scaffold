package org.scaffoldeditor.scaffold.level.render;

import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.level.entity.Entity;

/**
 * Tells the editor to render a "billboard". A billboard is a 2d texture that
 * has a position in 3d space. It's rendered on a plane at that position, always
 * facing the camera. Particles are a Minecraft example of billboards.
 * 
 * @author Igrium
 */
public class BillboardRenderEntity extends RenderEntity {
	
	private String texture;
	private float scale;
	
	/**
	 * Create a render entity that renders a billboard.
	 * 
	 * @param entity     Owning Scaffold entity.
	 * @param position   Position of the render entity.
	 * @param identifier The unique identifier of this render entity, used to keep
	 *                   track of it in relation to its editor entity. Only one
	 *                   instance of this string may exist per Scaffold entity.
	 *                   Different Scaffold entities may share identifiers.
	 * @param texture    Namespaced texture to display on the billboard
	 *                   (<code>[namespace]:textures/[texture].png</code>)
	 * @param scale      Scale to render the billboard at, where 1.0 = 1 block.
	 */
	public BillboardRenderEntity(Entity entity, Vector3dc position, String identifier, String texture, float scale) {
		super(entity, position, identifier);
		this.texture = texture;
		this.scale = scale;
	}
	
	/**
	 * Create a render entity that renders a billboard.
	 * 
	 * @param entity     Owning Scaffold entity.
	 * @param position   Position of the render entity.
	 * @param identifier The unique identifier of this render entity, used to keep
	 *                   track of it in relation to its editor entity. Only one
	 *                   instance of this string may exist per Scaffold entity.
	 *                   Different Scaffold entities may share identifiers.
	 * @param texture    Namespaced texture to display on the billboard
	 *                   (<code>[namespace]:textures/[texture].png</code>)
	 */
	public BillboardRenderEntity(Entity entity, Vector3dc position, String identifier, String texture) {
		this(entity, position, identifier, texture, 1);
	}

	public String getTexture() {
		return texture;
	}
	
	public float getScale() {
		return scale;
	}
	
	@Override
	public String toString() {
		return super.toString() + " texture: "+texture;
	}

}
