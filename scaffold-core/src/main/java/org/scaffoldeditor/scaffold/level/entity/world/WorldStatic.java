package org.scaffoldeditor.scaffold.level.entity.world;

import java.io.File;
import java.io.IOException;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollectionManager;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.Faceable;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.math.Vector;

/**
 * This entity compiles a standard block collection into the world.
 * @author Sam54123
 */
public class WorldStatic extends Faceable implements BlockEntity {
	
	private SizedBlockCollection model;
	
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
	
	@Override
	public void onUpdateAttributes() {
		super.onUpdateAttributes();
		reload();
	}
	
	/**
	 * Reload model from file.
	 */
	public void reload() {
		StringAttribute attribute = (StringAttribute) getAttribute("model");
		String model = attribute.getValue();
		System.out.println("Loading model " + model);
		if (model.contentEquals("")) {
			return;
		}
		
		File modelFile = getProject().assetManager().findAsset(model).toFile();
		
		getLevel().dirtyChunks.addAll(getOverlappingChunks(getLevel().getBlockWorld()));
		try {
			this.model = BlockCollectionManager.readFile(modelFile);
			getLevel().dirtyChunks.addAll(getOverlappingChunks(getLevel().getBlockWorld()));
		} catch (IOException e) {
			System.out.println("Unable to load model " + model);
			System.out.println(e.getMessage());
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
		
		world.addBlockCollection(model, (int) gridPos.X() , (int) gridPos.Y(), (int) gridPos.Z(), true);
		System.out.println("Added block collection to world.");
		
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
		return new Vector[] { position, Vector.add(position, new Vector(model.getWidth(), model.getHeight(), model.getLength())) };
	}
	
	@Override
	public void setPosition(Vector position) {	
		getLevel().dirtyChunks.addAll(getOverlappingChunks(getLevel().getBlockWorld()));
		super.setPosition(position);
		getLevel().dirtyChunks.addAll(getOverlappingChunks(getLevel().getBlockWorld()));
		if (getLevel().autoRecompile) {
			getLevel().quickRecompile();
		}
	}
}
