package org.scaffoldeditor.scaffold.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.math.MathUtils;

/**
 * A set of utility functions to be used on levels
 * @author Igrium
 */
public final class LevelOperations {
	private LevelOperations() {};
	
	/**
	 * Update a level's level stack, marking the necessary chunks as dirty.
	 * @param level Level to target.
	 * @param stack New version of the level stack. (clones this list; doesn't reference it)
	 * @param suppressUpdate Don't call entity stack listeners.
	 */
	@SuppressWarnings("deprecation")
	public static void modifyLevelStack(Level level, StackGroup stack, boolean suppressUpdate) {
		List<Entity> oldStack = level.getLevelStack().collapse();
		List<Entity> newStack = stack.collapse();
		Set<Entity> processed = new HashSet<>(); // The entities that have already been processed.
		
		for (Entity ent : oldStack) {
			if (!newStack.contains(ent)) {
				throw new IllegalArgumentException("New stack is missing entity: "+ent);
			}
		}
		
		for (int newIndex = 0; newIndex < newStack.size(); newIndex++) {
			Entity entity = newStack.get(newIndex);
			
			if (entity instanceof BlockEntity) {
				BlockEntity blockEntity = (BlockEntity) entity;
				int oldIndex = oldStack.indexOf(entity);
				// If it's not in the old stack, it means it was added.
				if (oldIndex == -1) {
					level.dirtySections.addAll(blockEntity.getOverlappingSections());
					
				} else {
					// Compare against other entities.
					for (int otherNewIndex = 0; otherNewIndex < newStack.size(); otherNewIndex++) {
						Entity otherEntity = newStack.get(otherNewIndex);
						
						// Only update if the other entity is a block entity and we haven't already processed it.
						if (otherEntity instanceof BlockEntity && !processed.contains(otherEntity)) {
							BlockEntity other = (BlockEntity) otherEntity;
							int otherOldIndex = oldStack.indexOf(otherEntity);
							
							// Only update if they've actually changed places relative to each other.
							if ((newIndex > otherNewIndex && oldIndex < otherOldIndex)
									|| (newIndex < otherNewIndex && oldIndex > otherOldIndex)) {
								addOverlap(level, blockEntity, other);
							}
						}
					}
				}
			}
			processed.add(entity);
		}
		
		level.setLevelStack(stack);
		if (!suppressUpdate) level.updateLevelStack();
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

	/**
	 * Make sure an entity name is valid (there are no duplicates), and return a
	 * new, valid name if it isn't.
	 * 
	 * @param name          Name to test.
	 * @param existingNames Names of all the entities in the level that we need to
	 *                      check against.
	 * @param ignore        If the name is in this list, return it as-is.
	 * @return Valid name.
	 */
	public static String validateName(String name, Set<String> existingNames, String... ignore) {
		if (Arrays.asList(ignore).contains(name)) {
			return name;
		}

		while (existingNames.contains(name)) {
			// Attempt to increment number
			if (Character.isDigit(name.charAt(name.length() - 1))) {
				int lastNum = Character.getNumericValue(name.charAt(name.length() - 1)) + 1;
				name = name.substring(0, name.length() - 1) + lastNum;
			} else {
				name = name + '1';
			}
		}
		return name;
	}
}
