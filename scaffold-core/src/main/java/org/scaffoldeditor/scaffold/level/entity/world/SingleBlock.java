package org.scaffoldeditor.scaffold.level.entity.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.block.Section;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.math.Vector;

import com.github.mryurihi.tbnbt.tag.NBTTagCompound;

/**
 * Places a single block into the world.
 * @author Sam54123
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
		attributes().put("blockName", new StringAttribute("minecraft:stone"));
		attributes().put("blockProperties", new NBTAttribute(new NBTTagCompound(new HashMap<>())));
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
	
	/**
	 * Get the grid position of this block.
	 * @return Grid position.
	 */
	public Vector gridPos() {
		return Vector.floor(getPosition());
	}

	@Override
	public boolean compileWorld(BlockWorld world, boolean full) {
		Vector gridPos = gridPos();
		world.setBlock((int) gridPos.X(), (int) gridPos.Y(), (int) gridPos.Z(), getBlock(), this);
		
		return false;
	}

	@Override
	public Block blockAt(Vector coord) {
		if (Vector.floor(coord).equals(gridPos())) {
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
	public Set<ChunkCoordinate> getOverlappingChunks(BlockWorld world) {
		Set<ChunkCoordinate> set = new HashSet<>();
		set.add(new ChunkCoordinate((int) Math.floor(getPosition().X() / Chunk.WIDTH), (int) Math.floor(getPosition().Z() / Chunk.LENGTH)));
		return set;
	}
	
	@Override
	public Set<SectionCoordinate> getOverlappingSections(BlockWorld world) {
		Set<SectionCoordinate> set = new HashSet<>();
		set.add(new SectionCoordinate((int) Math.floor(getPosition().X() / Chunk.WIDTH), (int) Math.floor(getPosition().Y() / Section.HEIGHT), (int) Math.floor(getPosition().Z() / Chunk.LENGTH)));
		return set;
	}
	
	@Override
	public void setPosition(Vector position) {	
		getLevel().dirtyChunks.addAll(getOverlappingChunks(getLevel().getBlockWorld()));
		super.setPosition(position);
		getLevel().dirtyChunks.addAll(getOverlappingChunks(getLevel().getBlockWorld()));
			
	}
}
