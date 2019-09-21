package org.metaversemedia.scaffold.level.entity;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.metaversemedia.scaffold.level.Level;

/**
 * Base entity class in maps
 * @author Sam54123
 *
 */
public class Entity {
	/* Position of the entity in world space */
	private Vector3D position;
	
	/* Name of the entity */
	String name;
	
	/* The level this entity belongs to */
	Level level;
	
	
}
