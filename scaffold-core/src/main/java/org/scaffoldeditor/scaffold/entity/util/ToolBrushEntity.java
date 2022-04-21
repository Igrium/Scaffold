package org.scaffoldeditor.scaffold.entity.util;

import java.util.Set;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.BrushEntity;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.render.BrushRenderEntity;
import org.scaffoldeditor.scaffold.render.RenderEntityManager;


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
	
	protected BrushRenderEntity visualiser;
	
	/**
	 * Get this brush's end point.
	 * @return The end point, relative to the position.
	 */
	public Vector3dc getEndPoint() {
		return endPoint.getValue();
	}
	
	/**
	 * Set this brush's end point.
	 * @param value The end point, relative to the position.
	 */
	public void setEndPoint(Vector3dc value) {
		setAttribute("end_point", new VectorAttribute(value));
	}

	@Override
	public void setBrushBounds(Vector3dc[] newBounds, boolean suppressUpdate) {
		Vector3dc start = newBounds[0];
		Vector3dc end = newBounds[1];
		
		setAttribute("position", new VectorAttribute(start));
		setAttribute("end_point", new VectorAttribute(end.sub(start, new Vector3d())));
	}

	@Override
	public Vector3dc[] getBrushBounds() {
		Vector3dc position = getPosition();
		return new Vector3dc[] { position, position.add(getEndPoint(), new Vector3d()) };
	}
	
	/**
	 * Get the texture the brush should render with.
	 * 
	 * @return The tool texture, in the format
	 *         <code>[namespace]:textures/[texture_name].png</code>
	 */
	public abstract String getTexture();

	@Override
	public void updateRenderEntities() {
		super.updateRenderEntities();

		if (visualiser == null) {
			visualiser = RenderEntityManager.getInstance().createBrush();
			visualiser.setTexture(getTexture());
			managedRenderEntities.add(visualiser);
		}

		visualiser.setStartPos(getPreviewPosition());
		visualiser.setEndPos(getEndPoint());
	}

	
	// @Override
	// public Set<RenderEntity> getRenderEntities() {
	// 	Set<RenderEntity> set = super.getRenderEntities();
	// 	Vector3dc position = getPreviewPosition();
	// 	set.add(new BrushRenderEntity(this, position, position.add(getEndPoint(), new Vector3d()), getTexture(), "brush_entity"));
	// 	return set;
	// }
}
