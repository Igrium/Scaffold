package org.scaffoldeditor.scaffold.level.entity.info;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityProvider;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EntityAttribute;
import org.scaffoldeditor.scaffold.level.entity.game.KnownUUID;
import org.scaffoldeditor.scaffold.level.entity.logic.LogicEntity;
import org.scaffoldeditor.scaffold.math.MathUtils;
import org.scaffoldeditor.scaffold.util.UUIDUtils;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.ListTag;

/**
 * Represents a point in a path through the level. Doesn't do anything on its
 * own, but contains data for entities at compile time and run time to work
 * with.
 * 
 * @author Igrium
 */
public class InfoPath extends LogicEntity implements KnownUUID, EntityProvider {
	
	public static void register() {
		EntityRegistry.registry.put("info_path", InfoPath::new);
	}
	
	public InfoPath(Level level, String name) {
		super(level, name);
	}

	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = new HashMap<>();
		map.put("next", new EntityAttribute(""));
 		return map;
	}
	
	
	public InfoPath getNext() {
		Entity ent = getLevel().getEntity(((EntityAttribute) getAttribute("next")).getValue());
		if (ent instanceof InfoPath) {
			return (InfoPath) ent;
		} else {
			return null;
		}
	}
	
	/**
	 * Generate the companion entity.
	 */
	public CompoundTag getEntity() {
		CompoundTag nbt = new CompoundTag();
		nbt.putIntArray("UUID", UUIDUtils.toIntArray(getUUID()));
		double[] rot = getRotation();
		ListTag<FloatTag> rotation = new ListTag<>(FloatTag.class);
		rotation.addFloat((float) rot[0]);
		rotation.addFloat((float) rot[1]);
		nbt.put("Rotation", rotation);
		nbt.putString("CustomName", "'"+getFinalName()+"'");
		
		CompoundTag data = new CompoundTag();
		InfoPath next = getNext();
		if (next != null) {
			data.putIntArray("next", UUIDUtils.toIntArray(next.getUUID()));
		}
		data.putString("scaffoldID", getFinalName());
		nbt.put("data", data);
		
		nbt.putString("id", "minecraft:marker");
		return nbt;
	}
	
	private double[] getRotation() {
		InfoPath target = getNext();
		if (target == null) return new double[] { 0, 0 };
		
		return MathUtils.getFacingAngle(target.getPosition().subtract(getPosition()).toDouble());
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/rail.png";
	}
	
	@Override
	public boolean isGridLocked() {
		return false;
	}

	@Override
	public UUID getUUID() {
		return getLevel().getCompanionUUID(this);
	}

	@Override
	public boolean compileGameEntities(BlockWorld world) {
		world.addEntity(getEntity(), getPosition().toDouble());
		return true;
	}

}
