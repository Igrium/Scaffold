package org.scaffoldeditor.scaffold.level.entity.game;

import java.util.UUID;

import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.util.UUIDUtils;

import net.querz.nbt.tag.CompoundTag;

/**
 * Represents a Minecraft entity who's UUID is known at compile time. If the
 * same UUID is applied to multiple entities, it can cause instability.
 * 
 * @author Igrium
 */
public interface KnownUUID extends TargetSelectable {
	@Override
	default TargetSelector getTargetSelector() {
		return TargetSelector.fromUUID(getUUID());
	}
	
	/**
	 * @return This entity's UUID.
	 */
	UUID getUUID();
	
	/**
	 * Create a wrapper around an existing UUID.
	 * @param in Subject UUID.
	 * @return The wrapper.
	 */
	public static KnownUUID getWrapper(UUID in) {
		return new KnownUUID() {
			
			@Override
			public UUID getUUID() {
				return in;
			}
		};
	}
	
	/**
	 * Create a UUID that represents a Minecraft entity. If the entity already
	 * contains a <code>UUID</code> tag, that UUID is used. Otherwise, a new, random
	 * UUID is generated and inserted into the entity.
	 * 
	 * @param entity Subject entity.
	 * @return UUID object.
	 */
	public static KnownUUID fromEntity(CompoundTag entity) {
		UUID uuid;
		if (entity.containsKey("UUID")) {
			uuid = UUIDUtils.fromIntArray(entity.getIntArray("UUID"));
		} else {
			uuid = UUID.randomUUID();
			entity.putIntArray("UUID", UUIDUtils.toIntArray(uuid));
		}
		return getWrapper(uuid);
	}
}
