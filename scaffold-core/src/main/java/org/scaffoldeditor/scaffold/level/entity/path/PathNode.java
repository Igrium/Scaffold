package org.scaffoldeditor.scaffold.level.entity.path;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityProvider;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EntityAttribute;
import org.scaffoldeditor.scaffold.level.entity.game.KnownUUID;
import org.scaffoldeditor.scaffold.level.entity.logic.LogicEntity;
import org.scaffoldeditor.scaffold.level.render.LineRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;
import org.scaffoldeditor.scaffold.logic.LogicUtils;
import org.scaffoldeditor.scaffold.math.MathUtils;
import org.scaffoldeditor.scaffold.sdoc.SDoc;
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
public class PathNode extends LogicEntity implements KnownUUID, EntityProvider {
	
	public static void register() {
		EntityRegistry.registry.put("path_node", PathNode::new);
	}
		
	public PathNode(Level level, String name) {
		super(level, name);
	}
	
	// for updating render entities
	private Vector3f nextPos;

	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = new HashMap<>();
		map.put("next", new EntityAttribute(""));
 		return map;
	}

	public PathNode getNext() {
		Entity ent = getLevel().getEntity(((EntityAttribute) getAttribute("next")).evaluate(this));
		if (ent instanceof PathNode) {
			return (PathNode) ent;
		} else {
			return null;
		}
	}
	
	public PathNode getPrevious() {
		for (Entity ent : getLevel().getLevelStack()) {
			if (ent instanceof PathNode && ent.getAttribute("next").getValue().equals(getName())) {
				return (PathNode) ent;
			}
		}
		return null;
	}
	
	/**
	 * Generate the companion entity.
	 */
	public CompoundTag getEntity() {
		CompoundTag ent = LogicUtils.getCompanionEntity(this);
		ListTag<FloatTag> rotation = new ListTag<>(FloatTag.class);
		double[] rot = getRotation();
		rotation.addFloat((float) rot[0]);
		rotation.addFloat((float) rot[1]);
		ent.put("Rotation", rotation);
		
		PathNode next = getNext();
		if (next != null) {
			ent.getCompoundTag("data").putIntArray("next", UUIDUtils.toIntArray(next.getUUID()));

		}
		return ent;
	}
	
	/**
	 * Get the rotation this path must be at to face it's target.
	 * 
	 * @return A two-element array indicating the calculated yaw and pitch in
	 *         aformat that can be plugged into Minecraft entities.
	 */
	public double[] getRotation() {
		PathNode target = getNext();
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
	
	@Override
	public Set<RenderEntity> getRenderEntities() {
		Set<RenderEntity> ents = super.getRenderEntities();
		PathNode next = getNext();
		if (next != null) {
			ents.add(new LineRenderEntity(this, getPreviewPosition(), next.getPreviewPosition(), "line", .72f, .48f, .09f, .82f));
			nextPos = next.getPreviewPosition();
		}
		PathNode previous = getPrevious();
		if (previous != null && !getPreviewPosition().equals(previous.nextPos)) {
			previous.updateRenderEntities();
		}
		return ents;
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/path_node.sdoc");
	}
}
