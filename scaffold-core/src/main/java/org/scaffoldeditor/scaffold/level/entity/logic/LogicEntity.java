package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Set;

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
		set.add(new BillboardRenderEntity(this, getPreviewPosition(), "sprite", getSprite()));
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
}
