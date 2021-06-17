package org.scaffoldeditor.scaffold.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.math.MathUtils;

/**
 * A set of utility functions to be used on levels
 * @author Igrium
 */
public final class LevelOperations {
	private LevelOperations() {};
	
	/**
	 * Update a level's entity stack, marking the necessary chunks as dirty.
	 * @param level Level to target.
	 * @param newStack New version of the level stack. (clones this list; doesn't reference it)
	 * @param suppressUpdate Don't call entity stack listeners.
	 */
	public static void modifyEntityStack(Level level, List<String> newStack, boolean suppressUpdate) {
		List<String> oldStack = level.getEntityStack();
		Set<String> processed = new HashSet<>(); // The entities that have already been processed.
		
		for (int newIndex = 0; newIndex < newStack.size(); newIndex++) {
			String name = newStack.get(newIndex);
			Entity entity = level.getEntity(name);
			
			if (entity instanceof BlockEntity) {
				BlockEntity blockEntity = (BlockEntity) entity;
				int oldIndex = oldStack.indexOf(name);
				// If it's not in the old stack, it means it was added.
				if (oldIndex == -1) {
					level.dirtySections.addAll(blockEntity.getOverlappingSections());
					
				} else {
					for (int otherNewIndex = 0; otherNewIndex < newStack.size(); otherNewIndex++) {
						String otherName = newStack.get(otherNewIndex);
						Entity otherEntity = level.getEntity(otherName);
						
						// Only update if the other entity is a block entity and we haven't already processed it.
						if (otherEntity instanceof BlockEntity && !processed.contains(otherName)) {
							BlockEntity other = (BlockEntity) otherEntity;
							int otherOldIndex = oldStack.indexOf(otherName);
							
							// Only update if they've actually changed places relative to each other.
							if ((newIndex > otherNewIndex && oldIndex < otherOldIndex)
									|| (newIndex < otherNewIndex && oldIndex > otherOldIndex)) {
								addOverlap(level, blockEntity, other);
							}
						}
					}
				}
			}
			processed.add(name);
		}
		
		oldStack.clear();
		oldStack.addAll(newStack);
		if (!suppressUpdate) level.updateEntityStack();
	}
	
	/**
	 * Mark all the sections where these two entities overlap as dirty.
	 */
	private static void addOverlap(Level level, BlockEntity ent1, BlockEntity ent2) {
		Vector3i[] ent1Bounds = ent1.getBounds();
		Vector3i[] ent2Bounds = ent2.getBounds();
		
		// We want to iterate over as few sections of possible; target the entity with the smallest volume.
		boolean bool = MathUtils.calculateVolume(ent1Bounds[0].toFloat(), ent1Bounds[1].toFloat()) < MathUtils
				.calculateVolume(ent2Bounds[0].toFloat(), ent2Bounds[1].toFloat());
		BlockEntity target = bool ? ent1 : ent2;
		BlockEntity subject = bool ? ent2 : ent1;
		Vector3i[] subjectBounds = subject.getBounds();
		
		for (SectionCoordinate c : target.getOverlappingSections()) {
			Vector3f sectionStart = new Vector3f(c.getStartX(), c.getStartY(), c.getStartZ());
			Vector3f sectionEnd = new Vector3f(c.getEndX() - 1, c.getEndY() - 1, c.getEndZ() - 1);
			
			if (MathUtils.detectCollision(sectionStart, sectionEnd, subjectBounds[0].toFloat(), subjectBounds[1].toFloat())) {
				level.dirtySections.add(c);
			}
		}
		
	}
}
