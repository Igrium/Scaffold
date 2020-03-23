package org.scaffoldeditor.scaffold.level.entity.world;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollectionManager;
import org.scaffoldeditor.nbt.block.BlockWorld;
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
	
}
