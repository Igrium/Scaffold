package org.scaffoldeditor.scaffold.level.entity.util;

import java.util.Set;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BrushEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.level.render.BrushRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

/**
 * Base class for brush entities that have no physical manifestation in the
 * compiled level.
 * 
 * @author Igrium
 */
public abstract class ToolBrushEntity extends Entity implements BrushEntity {

	public ToolBrushEntity(Level level, String name) {
		super(level, name);
	}

	@Attrib(name = "end_point")
	private VectorAttribute endPoint = new VectorAttribute(1, 1, 1);
	
	/**
	 * Get this brush's end point.
	 * @return The end point, relative to the position.
	 */
	public Vector3f getEndPoint() {
		return endPoint.getValue();
	}
	
	/**
	 * Set this brush's end point.
	 * @param value The end point, relative to the position.
	 */
	public void setEndPoint(Vector3f value) {
		setAttribute("end_point", new VectorAttribute(value));
	}

	@Override
	public void setBrushBounds(Vector3f[] newBounds, boolean suppressUpdate) {
		Vector3f start = newBounds[0];
		Vector3f end = newBounds[1];
		
		setAttribute("position", new VectorAttribute(start));
		setAttribute("end_point", new VectorAttribute(end.subtract(start)));
	}

	@Override
	public Vector3f[] getBrushBounds() {
		Vector3f position = getPosition();
		return new Vector3f[] { position, position.add(getEndPoint()) };
	}
	
	/**
	 * Get the texture the brush should render with.
	 * 
	 * @return The tool texture, in the format
	 *         <code>[namespace]:textures/[texture_name].png</code>
	 */
	public abstract String getTexture();
	
	@Override
	public Set<RenderEntity> getRenderEntities() {
		Set<RenderEntity> set = super.getRenderEntities();
		Vector3f position = getPreviewPosition();
		set.add(new BrushRenderEntity(this, position, position.add(getEndPoint()), getTexture(), "brush_entity"));
		return set;
	}
}
