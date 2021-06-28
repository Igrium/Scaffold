package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Set;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.render.BillboardRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

/**
 * A base class implementing some of the common functions found in logic entities.
 * @author Igrium
 *
 */
public abstract class LogicEntity extends Entity {

	public LogicEntity(Level level, String name) {
		super(level, name);
	}
	
	@Override
	public Set<RenderEntity> getRenderEntities() {
		Set<RenderEntity> set = super.getRenderEntities();
		set.add(new BillboardRenderEntity(this, getPreviewPosition().add(getRenderOffset()), "sprite", getSprite(),
				getRenderScale()));
		return set;
	}
	
	/**
	 * Get the sprite texture that this entity should use on its billboard.
	 * @return Texture ID in the form <code>[namespace]:textures/[texture].png</code>
	 */
	public abstract String getSprite();
	
	@Override
	public boolean isGridLocked() {
		/*
		 * While not techincally required, having logic entities stick to the grid makes
		 * things easier.
		 */
		return true;
	}
	
	/**
	 * Get the scale to render the billboard at.
	 */
	protected float getRenderScale() {
		return 1;
	}
	
	/**
	 * Get the offset from the entity origin to render the billboard.
	 */
	protected Vector3f getRenderOffset() {
		return new Vector3f(0, 0, 0);
	}
}
