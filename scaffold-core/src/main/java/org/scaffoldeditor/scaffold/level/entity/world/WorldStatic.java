package org.scaffoldeditor.scaffold.level.entity.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.block.transform.TransformSizedBlockCollection;
import org.scaffoldeditor.nbt.math.Matrix;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.scaffold.io.AssetTypeRegistry;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.Faceable;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.math.Vector;

/**
 * This entity compiles a standard block collection into the world.
 * @author Igrium
 */
public class WorldStatic extends BaseBlockEntity implements Faceable, BlockEntity {
	
	private SizedBlockCollection baseModel;
	private SizedBlockCollection finalModel;
	
	// Keep track of the model path, location, and directoin on our own for optimization.
	private String modelpath;
	private String direction = "";
	
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
		setAttribute("model", new StringAttribute(""), true);
		setAttribute("direction", new StringAttribute("NORTH"), true);
		setAttribute("place_air", new BooleanAttribute(false), true);
	}

	/**
	 * Reload model from file.
	 */
	public void reload() {
		
		StringAttribute attribute = (StringAttribute) getAttribute("model");
		String model = attribute.getValue();
		System.out.println("Loading model " + model);
		modelpath = model;
		if (model.length() == 0) {
			this.baseModel = null;
			return;
		}
		
		if (AssetTypeRegistry.isTypeAssignableTo(FilenameUtils.getExtension(model), SizedBlockCollection.class)) {
			try {
//				this.model = Structure.fromCompoundMap((CompoundTag) NBTUtil.read(modelFile).getTag());
				this.baseModel = (SizedBlockCollection) getProject().assetManager().loadAsset(model, false);
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			} catch (IOException e) {
				System.err.println("Unable to load model " + model);
				e.printStackTrace();
			}
			updateDirection();
			
		} else {
			System.out.println(FilenameUtils.getExtension(model));
			System.err.println("Unable to load model " + model + " because it is not a valid model format.");
		}
	}
	
	public String getDirection() {
		if (getAttribute("direction") instanceof StringAttribute) {
			return ((StringAttribute) getAttribute("direction")).getValue();
		} else {
			return "";
		}
	}
	
	protected void updateDirection() {
		String direction = getDirection();
		
		if (direction.equals("NORTH")) {
			finalModel = baseModel;
		} else if (direction.equals("WEST")) {
			finalModel = new TransformSizedBlockCollection(baseModel, Matrix.Direction.WEST);
		} else if (direction.equals("SOUTH")) {
			finalModel = new TransformSizedBlockCollection(baseModel, Matrix.Direction.SOUTH);
		} else if (direction.equals("EAST")) {
			finalModel = new TransformSizedBlockCollection(baseModel, Matrix.Direction.EAST);
		} else {
			setAttribute("direction", new StringAttribute("NORTH"), true);
			finalModel = baseModel;
		}
		this.direction = direction;
	}

	@Override
	public boolean compileWorld(BlockWorld world, boolean full, Set<SectionCoordinate> sections) {
		if (full) {
			reload();
		}
		
		if (finalModel == null) {
			return true;
		}
		
		if (!((StringAttribute) getAttribute("direction")).getValue().equals(direction)) {
			updateDirection();
		}
		
		Vector3i gridPos = getPosition().floor();
		if (sections == null) { // TODO: Smarter algorithm to determine which compilation method we should use.
			world.addBlockCollection(finalModel, (int) gridPos.x , (int) gridPos.y, (int) gridPos.z, true, shouldPlaceAir(), this);
		} else {
			for (SectionCoordinate coord : sections) {
				compileSection(world, coord);
			}
		}
		getPosition();
		return true;
	}
	
	/**
	 * Compile a single section of the model.
	 * @param world World to compile into.
	 * @param coord Global section coordinates.
	 */
	public void compileSection(BlockWorld world, SectionCoordinate coord) {
		Vector3i gridPos = getPosition().floor();
		boolean placeAir = shouldPlaceAir();
		for (int x = coord.getStartX(); x < coord.getEndX(); x++) {
			for (int y = coord.getStartY(); y < coord.getEndY(); y++) {
				for (int z = coord.getStartZ(); z < coord.getEndZ(); z++) {
					 Block block = finalModel.blockAt(x - gridPos.x, y - gridPos.y, z - gridPos.z);
					 if (block != null && (placeAir || !block.getName().equals("minecraft:air"))) {
						 world.setBlock(x, y, z, block, this);
					 }
				}
			}
		}
	}
	
	protected boolean shouldPlaceAir() {
		return ((BooleanAttribute) getAttribute("place_air")).getValue();
	}

	@Override
	public Block blockAt(Vector coord) {
		// TODO Implement this.
		return null;
	}

	@Override
	public Vector[] getBounds() {
		Vector position = getPosition();
		if (finalModel == null) return new Vector[] { position, position };
		return new Vector[] { new Vector(position.add(finalModel.getMin().toFloat())), new Vector(position.add(finalModel.getMax().toFloat())) };
	}

	@Override
	public void setDirection(String direction) {
		this.setAttribute("direction", new StringAttribute(direction));
	}


	@Override
	public void onUpdateBlockAttributes() {
		if (!((StringAttribute) getAttribute("model")).getValue().equals(modelpath)) {
			reload();	
		} else if (!getDirection().equals(direction)) {
			updateDirection();
		}
	}


	@Override
	public boolean needsRecompiling() {
		return true;
	}
}
