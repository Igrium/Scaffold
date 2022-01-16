package org.scaffoldeditor.scaffold.level.entity.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.block.WorldMath.SectionCoordinate;
import org.scaffoldeditor.nbt.block.transform.TransformSizedBlockCollection;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.block_textures.SerializableBlockTexture;
import org.scaffoldeditor.scaffold.block_textures.SingleBlockTexture;
import org.scaffoldeditor.scaffold.io.AssetLoaderRegistry;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.Faceable;
import org.scaffoldeditor.scaffold.level.entity.Macro;
import org.scaffoldeditor.scaffold.level.entity.attribute.AssetAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockTextureAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EnumAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EnumAttribute.DefaultEnums.Direction;
import org.scaffoldeditor.scaffold.math.MathUtils;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

/**
 * This entity compiles a standard block collection into the world.
 * @author Igrium
 */
public class WorldStatic extends BaseBlockEntity implements Faceable {
	
	private SizedBlockCollection baseModel;
	private SizedBlockCollection finalModel;
	
	// Keep track of the model path, location, and directoin on our own for optimization.
	private String modelpath;
	private Direction directionCache = Direction.NORTH;
	
	public static void register() {
		EntityRegistry.registry.put("world_static", new EntityFactory<Entity>() {		
			@Override
			public Entity create(Level level, String name) {
				return new WorldStatic(level, name);
			}
		});
	}

	@Attrib
	protected AssetAttribute model = new AssetAttribute("schematic", "");

	@Attrib
	protected EnumAttribute<Direction> direction = new EnumAttribute<>(Direction.NORTH);

	@Attrib(name = "place_air")
	protected BooleanAttribute placeAir = new BooleanAttribute(false);

	@Attrib(name = "texture_override")
	protected BooleanAttribute textureOverride = new BooleanAttribute(false);

	@Attrib
	protected BlockTextureAttribute texture = new BlockTextureAttribute(new SingleBlockTexture(new Block("minecraft:stone")));

	public WorldStatic(Level level, String name) {
		super(level, name);
	}
	
	@Override
	public List<Macro> getMacros() {
		List<Macro> macros = super.getMacros();
		macros.add(new Macro("Reload", () -> {
			getLevel().dirtySections.addAll(getOverlappingSections());
			reload();
			getLevel().dirtySections.addAll(getOverlappingSections());
			if (getLevel().autoRecompile) {
				getLevel().quickRecompile();
			}
		}));
		return macros;
	}

	/**
	 * Reload model from file.
	 */
	public void reload() {
		
		String model = this.model.getValue();
		LogManager.getLogger().info("Loading model " + model);
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
				LogManager.getLogger().error(e.getMessage());
			} catch (IOException e) {
				LogManager.getLogger().error("Unable to load model " + model);
				e.printStackTrace();
			}
			updateDirection();
			
		} else {
			LogManager.getLogger().error("Unable to load model " + model + " because it is not a valid model format.");
		}
	}
	
	public Direction getDirection() {
		return this.direction.getValue();
	}
	
	protected void updateDirection() {
		Direction direction = getDirection();
		
		switch (direction) {
		case NORTH:
			finalModel = baseModel;
			break;
		case WEST:
			finalModel = new TransformSizedBlockCollection(baseModel, MathUtils.WEST);
			break;
		case SOUTH:
			finalModel = new TransformSizedBlockCollection(baseModel, MathUtils.SOUTH);
			break;
		case EAST:
			finalModel = new TransformSizedBlockCollection(baseModel, MathUtils.EAST);
		}

		this.directionCache = direction;
	}

	@Override
	public boolean compileWorld(BlockWorld world, boolean full, Set<SectionCoordinate> sections) {
		if (full) {
			reload();
		}
		
		if (finalModel == null) {
			return true;
		}
		
		if (direction.getValue()!= directionCache) {
			updateDirection();
		}
		
		Vector3ic gridPos = getBlockPosition();
		if (sections == null) { // TODO: Smarter algorithm to determine which compilation method we should use.
			if (textureOverrideEnabled()) {
				for (Vector3ic local : finalModel) {
					Vector3ic global = local.add(getBlockPosition(), new Vector3i());
					if (!finalModel.blockAt(local).getName().equals("minecraft:air")) {
						world.setBlock(global.x(), global.y(), global.z(), getTexture().blockAt(global.x(), global.y(), global.z()), this);
					}
				}
			} else {
				world.addBlockCollection(finalModel, gridPos.x() , gridPos.y(), gridPos.z(), true, shouldPlaceAir(), this);
			}
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
		Vector3ic gridPos = getBlockPosition();
		boolean placeAir = shouldPlaceAir();
		for (int x = coord.getStartX(); x < coord.getEndX(); x++) {
			for (int y = coord.getStartY(); y < coord.getEndY(); y++) {
				for (int z = coord.getStartZ(); z < coord.getEndZ(); z++) {
					 Block block = finalModel.blockAt(x - gridPos.x(), y - gridPos.y(), z - gridPos.z());
					 if (textureOverrideEnabled()) {
						 if (block != null && !block.getName().equals("minecraft:air")) {
							 world.setBlock(x, y, z, getTexture().blockAt(x, y, z));
						 }
					 } else {
						 if (block != null && (placeAir || !block.getName().equals("minecraft:air"))) {
							 world.setBlock(x, y, z, block, this);
						 } 
					 }
				}
			}
		}
	}
	
	protected boolean shouldPlaceAir() {
		return placeAir.getValue();
	}

	@Override
	public Block blockAt(Vector3ic coord) {
		Vector3i localCoord = coord.sub(getBlockPosition(), new Vector3i());
		return finalModel.blockAt(localCoord);
	}

	@Override
	public Vector3ic[] getBounds() {
		Vector3ic position = getBlockPosition();
		if (finalModel == null) return new Vector3ic[] { position, position };
		return new Vector3i[] { position.add(finalModel.getMin(), new Vector3i()),position.add(finalModel.getMax(), new Vector3i()) };
	}

	@Override
	public void setDirection(Direction direction) {
		this.setAttribute("direction", new EnumAttribute<>(direction));
	}


	@Override
	public void updateBlocks() {
		if (!model.getValue().equals(modelpath)) {
			reload();	
		} else if (!getDirection().equals(directionCache)) {
			updateDirection();
		}
	}

	@Override
	public boolean needsRecompiling() {
		return true;
	}
	
	/**
	 * Get the base block collection that this entity is loading.
	 * 
	 * @return The base model, or <code>null</code> if it's not loaded.
	 */
	public SizedBlockCollection getBaseModel() {
		return baseModel;
	}
	
	/**
	 * Get the final block collectino that this entity will use to compile, after
	 * any modifications are applied.
	 * 
	 * @return The final model, or <code>null</code> if it hasn't been processed
	 *         yet.
	 */
	public SizedBlockCollection getFinalModel() {
		return finalModel;
	}
	
	/**
	 * Get whether Texture Override is enabled.
	 */
	public boolean textureOverrideEnabled() {
		return (Boolean) getAttribute("texture_override").getValue();
	}
	
	/**
	 * Get the block texture to use if Texture Override is enabled.
	 */
	public SerializableBlockTexture getTexture() {
		return (SerializableBlockTexture) getAttribute("texture").getValue();
	}

	@Override
	public BlockCollection getBlockCollection() {
		return getFinalModel();
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getProject().assetManager(), "doc/world_static.sdoc", super.getDocumentation());
	}
}
