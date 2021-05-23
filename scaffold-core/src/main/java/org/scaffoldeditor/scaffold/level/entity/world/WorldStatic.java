package org.scaffoldeditor.scaffold.level.entity.world;

import java.io.File;
import java.io.IOException;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollectionManager;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.schematic.Structure;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.Faceable;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.math.Vector;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;

/**
 * This entity compiles a standard block collection into the world.
 * @author Sam54123
 */
public class WorldStatic extends BaseBlockEntity implements Faceable, BlockEntity {
	
	private SizedBlockCollection model;
	
	// Keep track of the model path and location on our own for optimization.
	private String modelpath;
	private Vector compiledLocation = new Vector(0,0,0);
	
	public static void Register() {
		EntityRegistry.registry.put("world_static", new EntityFactory<Entity>() {		
			@Override
			public Entity create(Level level, String name) {
				return new WorldStatic(level, name);
			}
		});
	}


	public WorldStatic(Level level, String name) {
		super(level, name);
		System.out.println("Constructing world static");
		setAttribute("model", new StringAttribute("/"), true);
	}

	
	/**
	 * Reload model from file.
	 */
	public void reload() {
		StringAttribute attribute = (StringAttribute) getAttribute("model");
		String model = attribute.getValue();
		System.out.println("Loading model " + model);
		modelpath = model;
		if (model.contentEquals("")) {
			return;
		}
		
		File modelFile = getProject().assetManager().findAsset(model).toFile();
		
		getLevel().dirtyChunks.addAll(getOverlappingChunks(getLevel().getBlockWorld()));
		try {
//			this.model = Structure.fromCompoundMap((CompoundTag) NBTUtil.read(modelFile).getTag());
			this.model = BlockCollectionManager.readFile(modelFile);
			getLevel().dirtyChunks.addAll(getOverlappingChunks(getLevel().getBlockWorld()));
		} catch (IOException e) {
			System.out.println("Unable to load model " + model);
			e.printStackTrace();
		}
	}

	@Override
	public boolean compileWorld(BlockWorld world, boolean full) {
		if (full) {
			reload();
		}
		
		if (model == null) {
			return true;
		}
		
		Vector gridPos = Vector.floor(getPosition());	
		world.addBlockCollection(model, (int) gridPos.X() , (int) gridPos.Y(), (int) gridPos.Z(), true, this);
		compiledLocation = getPosition();
		
		return true;
	}

	@Override
	public Block blockAt(Vector coord) {
		// TODO Implement this.
		return null;
	}

	@Override
	public Vector[] getBounds() {
		Vector position = getPosition();
		if (model == null) return new Vector[] { position, position };
		return new Vector[] { position, Vector.add(position, new Vector(model.getWidth(), model.getHeight(), model.getLength())) };
	}

	@Override
	public void setDirection(String direction) {
		this.setAttribute("direction", new StringAttribute(direction));
	}


	@Override
	public void onUpdateBlockAttributes() {
		if (!((StringAttribute) getAttribute("model")).getValue().equals(modelpath)) {
			reload();	
		}
	}


	@Override
	public boolean needsRecompiling() {
		return (!compiledLocation.equals(getPosition()) || !((StringAttribute) getAttribute("model")).getValue().equals(modelpath));
	}
}
