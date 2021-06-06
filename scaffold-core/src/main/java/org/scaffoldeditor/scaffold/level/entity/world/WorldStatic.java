package org.scaffoldeditor.scaffold.level.entity.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.block.transform.TransformSizedBlockCollection;
import org.scaffoldeditor.nbt.math.Matrix;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.scaffold.io.AssetLoaderRegistry;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.Faceable;
import org.scaffoldeditor.scaffold.level.entity.attribute.AssetAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EnumAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EnumAttribute.DefaultEnums.Direction;
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
	private Direction direction = Direction.NORTH;
	
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
		setAttribute("model", new AssetAttribute("schematic", ""), true);
		setAttribute("direction", new EnumAttribute<>(Direction.NORTH), true);
		setAttribute("place_air", new BooleanAttribute(false), true);
	}

	/**
	 * Reload model from file.
	 */
	public void reload() {
		
		AssetAttribute attribute = (AssetAttribute) getAttribute("model");
		String model = attribute.getValue();
		System.out.println("Loading model " + model);
		modelpath = model;
		if (model.length() == 0) {
			this.baseModel = null;
			return;
		}
		
		if (AssetLoaderRegistry.getAssetLoader(model).isAssignableTo(SizedBlockCollection.class)) {
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
			System.err.println("Unable to load model " + model + " because it is not a valid model format.");
		}
	}
	
	public Direction getDirection() {
		if (getAttribute("direction") instanceof EnumAttribute
				&& ((EnumAttribute<?>) getAttribute("direction")).getValue() instanceof Direction) {
			return (Direction) ((EnumAttribute<?>) getAttribute("direction")).getValue();
		} else {
			return Direction.NORTH;
		}
	}
	
	protected void updateDirection() {
		Direction direction = getDirection();
		
		switch (direction) {
		case NORTH:
			finalModel = baseModel;
			break;
		case WEST:
			finalModel = new TransformSizedBlockCollection(baseModel, Matrix.Direction.WEST);
			break;
		case SOUTH:
			finalModel = new TransformSizedBlockCollection(baseModel, Matrix.Direction.SOUTH);
			break;
		case EAST:
			finalModel = new TransformSizedBlockCollection(baseModel, Matrix.Direction.EAST);
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
		
		if (((EnumAttribute<?>) getAttribute("direction")).getValue() != direction) {
			updateDirection();
		}
		
		Vector3i gridPos = getPosition().floor();
		if (sections == null) { // TODO: Smarter algorithm to determine which compilation method we should use.
			world.addBlockCollection(finalModel, gridPos.x , gridPos.y, gridPos.z, true, shouldPlaceAir(), this);
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
		Vector3i localCoord = coord.subtract(getPosition()).floor();
		return finalModel.blockAt(localCoord);
	}

	@Override
	public Vector[] getBounds() {
		Vector position = getPosition();
		if (finalModel == null) return new Vector[] { position, position };
		return new Vector[] { new Vector(position.add(finalModel.getMin().toFloat())), new Vector(position.add(finalModel.getMax().toFloat())) };
	}

	@Override
	public void setDirection(Direction direction) {
		this.setAttribute("direction", new EnumAttribute<>(direction));
	}


	@Override
	public void onUpdateBlockAttributes() {
		if (!((AssetAttribute) getAttribute("model")).getValue().equals(modelpath)) {
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
