package org.scaffoldeditor.scaffold.entity;

import org.scaffoldeditor.scaffold.entity.attribute.EnumAttribute.DefaultEnums.Direction;

/**
 * This type of entity can face any of the four cardinal directions,
 * but cannot have arbitrary rotations
 * 
 * @author Igrium
 *
 */
public interface Faceable {
	public void setDirection(Direction direction);
}
