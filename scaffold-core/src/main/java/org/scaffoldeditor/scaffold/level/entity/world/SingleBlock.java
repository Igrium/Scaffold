package org.scaffoldeditor.scaffold.level.entity.world;

import java.util.HashMap;
import java.util.List;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.math.Vector;

import com.github.mryurihi.tbnbt.tag.NBTTag;
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
		attributes().put("blockName", "minecraft:stone");
		attributes().put("blockProperties", new NBTTagCompound(new HashMap<String, NBTTag>()));
	}
	
	@Override
	public List<AttributeDeclaration> getAttributeFields() {
		List<AttributeDeclaration> attributeFields = super.getAttributeFields();
		attributeFields.add(new AttributeDeclaration("blockName", String.class));
		attributeFields.add(new AttributeDeclaration("blockProperties", NBTTagCompound.class));

		return attributeFields;
	}
	
	/**
	 * Get the block this entity represents.
	 * @return
	 */
	public Block getBlock() {
		return new Block((String) getAttribute("blockName"), (NBTTagCompound) getAttribute("blockProperties"));
	}
	
	/**
	 * Set the block this entity represents.
	 * @param block Block to set.
	 */
	public void setBlock(Block block) {
		setAttribute("blockName", block.getName());
		setAttribute("blockProperties", block.getProperties());
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
		world.setBlock((int) gridPos.X(), (int) gridPos.Y(), (int) gridPos.Z(), getBlock());
		
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

}
