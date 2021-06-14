package org.scaffoldeditor.scaffold.level.entity.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.math.Vector;

import net.querz.nbt.tag.CompoundTag;

/**
 * Places a single block into the world.
 * @author Igrium
 */
public class SingleBlock extends Entity implements BlockEntity {
	
	public static void Register() {
		EntityRegistry.registry.put("single_block", new EntityFactory<Entity>() {		
			@Override
			public Entity create(Level level, String name) {
				return new SingleBlock(level, name);
			}
		});
	}
	
	public SingleBlock(Level level, String name) {
		super(level, name);
	}
	
	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = new HashMap<>();
		map.put("blockName", new StringAttribute("minecraft:stone"));
		map.put("blockProperties", new NBTAttribute(new CompoundTag()));
		return map;
	}
	
	/**
	 * Get the block this entity represents.
	 * @return
	 */
	public Block getBlock() {
		return new Block(((StringAttribute) getAttribute("blockName")).getValue(),
				((NBTAttribute) getAttribute("blockProperties")).getValue());
	}
	
	/**
	 * Set the block this entity represents.
	 * @param block Block to set.
	 */
	public void setBlock(Block block) {
		setAttribute("blockName", new StringAttribute(block.getName()));
		setAttribute("blockProperties", new NBTAttribute(block.getProperties()));
	}

	@Override
	public boolean compileWorld(BlockWorld world, boolean full, Set<SectionCoordinate> sections) {
		Vector3i gridPos = getPosition().floor();
		world.setBlock(gridPos.x, gridPos.y, gridPos.z, getBlock(), this);
		
		return false;
	}

	@Override
	public Block blockAt(Vector coord) {
		if (coord.floor().equals(getPosition().floor())) {
			return getBlock();
		} else {
			return null;
		}
	}

	@Override
	public Vector[] getBounds() {
		return new Vector[] {getPosition(), Vector.add(getPosition(), new Vector(1,1,1))};
	}
	
	@Override
	public Set<SectionCoordinate> getOverlappingSections() {
		Set<SectionCoordinate> set = new HashSet<>();
		set.add(new SectionCoordinate(getPosition().floor()));
		return set;
	}
	
	@Override
	public void setPosition(Vector position) {	
		getLevel().dirtySections.add(new SectionCoordinate(getPosition().floor()));
		super.setPosition(position);
		getLevel().dirtySections.add(new SectionCoordinate(position.floor()));
	}
}
