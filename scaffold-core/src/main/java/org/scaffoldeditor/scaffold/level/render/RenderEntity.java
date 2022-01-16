package org.scaffoldeditor.scaffold.level.render;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.level.entity.Entity;

/**
 * An abstraction allowing Scaffold entities to tell the editor how they should
 * be rendered.
 * 
 * @author Igrium
 */
public abstract class RenderEntity {
	private Entity entity;
	private Vector3dc position;
	private Vector3dc rotation;
	private String identifier;
	
	/**
	 * Create a render entity.
	 * 
	 * @param entity     Owning Scaffold entity.
	 * @param position   Position of the render entity.
	 * @param identifier The unique identifier of this render entity, used to keep
	 *                   track of it in relation to its editor entity. Only one
	 *                   instance of this string may exist per Scaffold entity.
	 *                   Different Scaffold entities may share identifiers.
	 */
	public RenderEntity(Entity entity, Vector3dc position, String identifier) {
		this(entity, position, new Vector3d(), identifier);
	}
	
	
	/**
	 * Create a render entity.
	 * 
	 * @param entity     Owning Scaffold entity.
	 * @param position   Position of the render entity.
	 * @param rotation   The pitch/yaw/roll of this entity when applicable. Note:
	 *                   some implementations only support pitch and yaw. In these
	 *                   cases, roll will always read as zero.
	 * @param identifier The unique identifier of this render entity, used to keep
	 *                   track of it in relation to its editor entity. Only one
	 *                   instance of this string may exist per Scaffold entity.
	 *                   Different Scaffold entities may share identifiers.
	 */
	public RenderEntity(Entity entity, Vector3dc position, Vector3dc rotation, String identifier) {
		this.entity = entity;
		this.position = position;
		this.rotation = rotation;
		this.identifier = identifier;
	}
	
	/**
	 * Get the Scaffold entity that this render entity represents
	 */
	public Entity getEntity() {
		return entity;
	};
	
	/**
	 * Get the position of this render entity.
	 * @return Position in global space.
	 */
	public  Vector3dc getPosition() {
		return position;
	};
	
	/**
	 * Get the rotation of this entity.
	 * 
	 * @return The pitch/yaw/roll of this entity when applicable. Note: some
	 *         implementations only support pitch and yaw. In these cases, roll will
	 *         always be zero.
	 */
	public Vector3dc getRotation() {
		return rotation;
	}
	
	/**
	 * Get the identifier of this render entity, used to keep track of it in
	 * relation to its editor entity.
	 * 
	 * @return The unique identifier string. Only one instance of this string may
	 *         exist per Scaffold entity. Different Scaffold entities may share
	 *         identifiers.
	 */
	public String identifier() {
		return identifier;
	}
	
	@Override
	public String toString() {
		return "Render entity: '"+identifier+"' at: "+position;
	}
}
