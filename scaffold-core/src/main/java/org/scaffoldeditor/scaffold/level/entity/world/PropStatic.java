package org.scaffoldeditor.scaffold.level.entity.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollectionManager;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Faceable;
import org.scaffoldeditor.scaffold.math.Vector;

/**
 * This entity compiles a standard block collection into the world.
 * @author Sam54123
 */
public class PropStatic extends Faceable implements BlockEntity {
	
	private SizedBlockCollection model;

	public PropStatic(Level level, String name) {
		super(level, name);
		setAttribute("model", "");
	}
	
	@Override
	public List<AttributeDeclaration> getAttributeFields() {
		List<AttributeDeclaration> attributes = super.getAttributeFields();
		attributes.add(new AttributeDeclaration("model", String.class));
		return attributes;
	}
	
	@Override
	protected void onUpdateAttributes() {
		super.onUpdateAttributes();
		reload();
	}
	
	/**
	 * Reload model from file.
	 */
	public void reload() {
		String model = (String) getAttribute("model");	
		System.out.println("Loading model " + model);
		if (model.contentEquals("")) {
			return;
		}
		
		File modelFile = getProject().assetManager().findAsset(model).toFile();
		
		try {
			this.model = BlockCollectionManager.readFile(modelFile);
		} catch (IOException e) {
			System.out.println("Unable to load model " + model);
			System.out.println(e.getMessage());
		}
	}

	@Override
	public boolean compileWorld(BlockWorld world) {
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
		if (model == null) {
			return null;
		}
		
		Vector localCoord = Vector.floor(Vector.subtract(coord, this.getPosition()));
		return model.blockAt((int) localCoord.X(), (int) localCoord.Y(), (int) localCoord.Z());
	}

	@Override
	public boolean recompile(boolean full) {
		if (full) {
			reload();
		}
		// This entity is simple and doesn't need to do anything else.
		return true;
	}

	@Override
	public Collection<ChunkCoordinate> getOccupiedChunks() {
		if (model == null) {
			return new ArrayList<ChunkCoordinate>();
		}
		
		// GET ALL CHUNKS IN BOUNDS
		Collection<ChunkCoordinate> boundChunks = new ArrayList<ChunkCoordinate>();
		
		// Get bounds.
		Vector v1 = Vector.floor(getPosition());
		Vector v2 = new Vector(
				v1.X() + model.getWidth(),
				v1.Y() + model.getHeight(),
				v1.Z() + model.getWidth());
		
		System.out.println(v1); // TESTING ONLY
		System.out.println(v2); // TESTING ONLY
		
		// Put into chunk coordinates
		Vector c1 = Vector.floor(Vector.divide(v1, Chunk.WIDTH));
		Vector c2 = Vector.floor(Vector.divide(v2, Chunk.LENGTH));
		
		for (int x = (int) c1.X(); x <= c2.X(); x++) {
			for (int z = (int) c1.Z(); z <= c2.Z(); z++) {
				boundChunks.add(new ChunkCoordinate(x, z));
			}
		}
		
		// TODO: Search chunks for blocks.
		
		return boundChunks;
	}
	
}
