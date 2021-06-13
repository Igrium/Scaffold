package org.scaffoldeditor.scaffold.level.render;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.util.MCEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;

/**
 * Represents an entity that can be simply rendered as a Minecraft entity.
 * Supports both vanilla and modded entities.
 * 
 * @author Igrium
 */
public class MCRenderEntity extends RenderEntity {
	
	private MCEntity mcEntity;
	
	/**
	 * Create a Minecraft render entity.
	 * 
	 * @param entity     Owning Scaffold entity.
	 * @param position   Position of the render entity.
	 * @param rotation   The pitch/yaw/roll of this entity when applicable. Note:
	 *                   some implementations only support pitch and yaw. In these
	 *                   cases, roll will always read as zero.
	 * @param mcEntity   Minecraft entity to render.
	 * @param identifier The unique identifier of this render entity, used to keep
	 *                   track of it in relation to its editor-side representation.
	 *                   Only one instance of this string may exist per Scaffold
	 *                   entity. Different Scaffold entities may share identifiers.
	 */
	public MCRenderEntity(Entity entity, Vector3f position, Vector3f rotation, MCEntity mcEntity, String identifier) {
		super(entity, position, rotation, identifier);
		this.mcEntity = mcEntity;
	}
	
	public MCEntity getMcEntity() {
		return mcEntity;
	}
	
	@Override
	public String toString() {
		return super.toString()+" mc_entity: "+mcEntity;
	}
}
