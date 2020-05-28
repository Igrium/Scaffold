package org.scaffoldeditor.scaffold.level.entity;

import org.scaffoldeditor.scaffold.math.Vector;

public class EntityUtils {
	
	/**
	 * Check if a block entity collides with a box. MUST BE COMPILED BEFOREHAND
	 * @param entity Entity to check.
	 * @param v1 Corner 1 of the box.
	 * @param v2 Corner 2 of the box.
	 * @return Does entity collide?
	 */
	public boolean checkCollision(BlockEntity entity, Vector v1, Vector v2) {
		// TODO: Test this.
		for (float y = Math.min(v1.Y(), v2.Y()); y < Math.max(v1.Y(), v2.Y()); y++) {
			for (float x = Math.min(v1.X(), v2.X()); x < Math.max(v1.X(), v2.X()); x++) {
				for (float z = Math.min(v1.Z(), v2.Z()); z < Math.max(v1.Z(), v2.Z()); z++) {
					if (entity.blockAt(new Vector(x, y, z)) != null);
				}
			}
		}
		
		return false;
		
	}
}
