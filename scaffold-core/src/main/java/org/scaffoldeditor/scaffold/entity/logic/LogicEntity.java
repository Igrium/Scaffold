package org.scaffoldeditor.scaffold.entity.logic;

import java.util.Set;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.render.BillboardRenderEntity;
import org.scaffoldeditor.scaffold.render.RenderEntityManager;


/**
 * A base class implementing some of the common functions found in logic entities.
 * @author Igrium
 *
 */
public abstract class LogicEntity extends Entity {

	public LogicEntity(Level level, String name) {
		super(level, name);
	}

	protected BillboardRenderEntity sprite;
	
	// @Override
	// public Set<RenderEntity> getRenderEntities() {
	// 	Set<RenderEntity> set = super.getRenderEntities();
	// 	set.add(new BillboardRenderEntity(this, getPreviewPosition().add(getRenderOffset(), new Vector3d()), "sprite", getSprite(),
	// 			getRenderScale()));
	// 	return set;
	// }

	@Override
	public void updateRenderEntities() {
		super.updateRenderEntities();
		if (sprite == null) {
			sprite = RenderEntityManager.getInstance().createBillboard();
			managedRenderEntities.add(sprite);
		}

		sprite.setTexture(getSprite());
		sprite.setPosition(getPreviewPosition());
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
	protected Vector3dc getRenderOffset() {
		return new Vector3d(0, 0, 0);
	}
}
