package org.scaffoldeditor.scaffold.entity.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.EntityProvider;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.attribute.EntityAttribute;
import org.scaffoldeditor.scaffold.entity.game.KnownUUID;
import org.scaffoldeditor.scaffold.entity.logic.LogicEntity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
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
	
	public static final String PASSED_OUTPUT = "on_passed";
	
	// for updating render entities
	private Vector3dc nextPos;

	@Attrib
	protected EntityAttribute next = new EntityAttribute("");
	
	@Override
	public Collection<OutputDeclaration> getDeclaredOutputs() {
		Collection<OutputDeclaration> out = super.getDeclaredOutputs();
		out.add(() -> PASSED_OUTPUT);
		return out;
	};
	
	/**
	 * Get the next node in the path.
	 * @return The next node, or {@code null} if there is no connection.
	 */
	public PathNode getNext() {
		Entity ent = getLevel().getEntity(((EntityAttribute) getAttribute("next")).evaluate(this));
		if (ent instanceof PathNode) {
			return (PathNode) ent;
		} else {
			return null;
		}
	}
	
	/**
	 * Get the previous node in the path.
	 * <br>
	 * <b>Warning:</b> Significantly more expensive that {@code getNext()}
	 * @return The previous node, or {@code null} of no node has a connection to this.
	 */
	public PathNode getPrevious() {
		for (Entity ent : getLevel().getLevelStack()) {
			if (ent instanceof PathNode && ent.getAttribute("next").getValue().equals(getName())) {
				return (PathNode) ent;
			}
		}
		return null;
	}
	
	/**
	 * Get a list of all nodes in this path. If the path loops, the list starts at
	 * this node. Otherwise, it starts at the first node.
	 * 
	 * @return Nodes in the path, in order.
	 */
	public List<PathNode> getPath() {
		List<PathNode> next = new ArrayList<>();
		PathNode current = this;
		while (current != null) {
			next.add(current);
			current = current.getNext();
			// Handle loops.
			if (current == this) {
				return next;
			}
		}
		
		PathNode first = getPrevious();
		if (first == null) return next; // We're the first node.
		
		PathNode previousNode = first.getPrevious();
		while (previousNode != null) {
			first = previousNode;
			previousNode = first.getPrevious();
		}
		
		return first.getPath();
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
		
		return MathUtils.getFacingAngle(target.getPosition().sub(getPosition(), new Vector3d()));
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
		world.addEntity(getEntity(), getPosition());
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
		return SDoc.loadAsset(getAssetManager(), "doc/path_node.sdoc", super.getDocumentation());
	}
}
