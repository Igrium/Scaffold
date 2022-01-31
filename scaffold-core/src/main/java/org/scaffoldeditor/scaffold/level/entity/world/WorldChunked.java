package org.scaffoldeditor.scaffold.level.entity.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.block.WorldMath.SectionCoordinate;
import org.scaffoldeditor.nbt.math.MathUtils;
import org.scaffoldeditor.nbt.block.ChunkedBlockCollection;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.io.AssetLoaderRegistry;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;

/**
 * A block entity which is optimized for chunked block collections, 
 * potentially infinite in size.
 * 
 * @author Igrium
 * @deprecated Doesn't work right now.
 */
@Deprecated
public class WorldChunked extends BaseBlockEntity {
	
	public static final String REGISTRY_NAME = "world_chunked";
	
	public static void register() {
		EntityRegistry.registry.put(REGISTRY_NAME, new EntityFactory<Entity>() {
			
			@Override
			public Entity create(Level level, String name) {
				return new WorldChunked(level, name);
			}
		});
	}
	
	private ChunkedBlockCollection modelCache;
	
	// Keep track of the model path on our own for optimization.
	private String modelpath;
	private Vector3ic[] boundsCache = new Vector3ic[] {new Vector3i(0,0,0), new Vector3i(0,0,0)};

	public WorldChunked(Level level, String name) {
		super(level, name);
	}

	@Attrib
	private StringAttribute model = new StringAttribute("");

	@Attrib(name = "place_air")
	private BooleanAttribute placeAir = new BooleanAttribute(false);
	
	/**
	 * Reload model from file.
	 */
	public void reload() {
		
		String model = this.model.getValue();
		LogManager.getLogger().info("Loading model " + model);
		modelpath = model;
		if (model.length() == 0) {
			this.modelCache = null;
			return;
		}
		
		if (AssetLoaderRegistry.isTypeAssignableTo(FilenameUtils.getExtension(model), ChunkedBlockCollection.class)) {
			try {
//				this.model = Structure.fromCompoundMap((CompoundTag) NBTUtil.read(modelFile).getTag());
				this.modelCache = (ChunkedBlockCollection) getProject().assetManager().loadAsset(model, false);
				onLoadModel();
			} catch (FileNotFoundException e) {
				LogManager.getLogger().error(e.getMessage());
			} catch (IOException e) {
				LogManager.getLogger().error("Unable to load model " + model);
				e.printStackTrace();
			}
			
		} else {
			LogManager.getLogger().error("Unable to load model " + model + " because it is not a valid model format.");
		}
	}
	
	protected void onLoadModel() {
		// Calculate the entity bounds
		if (modelCache == null || modelCache.getSections().size() == 0) return;
		
		Vector3ic def = modelCache.getSections().iterator().next();
		
		int minX = def.x();
		int minY = def.y();
		int minZ = def.z();
		
		int maxX = def.x();
		int maxY = def.y();
		int maxZ = def.z();
		
		int width = modelCache.getSectionWidth();
		int height = modelCache.getSectionHeight();
		int length = modelCache.getSectionLength();
		
		for (Vector3ic section : modelCache.getSections()) {
			if (section.x() < minX) minX = section.x();
			if (section.y() < minY) minY = section.y();
			if (section.z() < minZ) minZ = section.z();
			
			if (section.x() > maxX) maxX = section.x();
			if (section.y() > maxY) maxY = section.y();
			if (section.z() > maxZ) maxZ = section.z();
		}
		
		boundsCache[0] = new Vector3i(minX * width, minY * height, minZ * length);
		boundsCache[1] = new Vector3i(maxX * width, maxY * height, maxZ * length);
	}

	@Override
	public boolean compileWorld(BlockWorld world, boolean full, Set<SectionCoordinate> worldSections) {
		if (full) reload();
		if (modelCache == null) return false;
		
		int width = modelCache.getSectionWidth();
		int length = modelCache.getSectionLength();
		int height = modelCache.getSectionHeight();
		Vector3ic position = getBlockPosition();
		
		if (worldSections == null) {
			// Compile entire model
			for (Vector3ic coord : modelCache.getSections()) {
				SizedBlockCollection section = modelCache.sectionAt(coord.x(), coord.y(), coord.z());
				world.addBlockCollection(section, coord.x() * width + position.x(), coord.y() * height + position.y(),
						coord.z() * length + position.z(), true, shouldPlaceAir(), this);
			}
			return true;
		} else {
			// Compile selection set
			Set<Vector3i> updatingModelSections = new HashSet<>();
			if (isAligned()) {
				for (SectionCoordinate coord : worldSections) {
					updatingModelSections
							.add(coord.sub(world.getSection(position), new Vector3i()));
				}
			} else {
				// Identify the collection sections that the world sections overlap with.
				for (SectionCoordinate coord : worldSections) {
					Vector3ic minModelSection = modelCache.getSection(new Vector3i(coord.getStartX(), coord.getStartY(), coord.getStartZ()).sub(position));
					Vector3ic maxModelSection = modelCache.getSection(new Vector3i(coord.getEndX(), coord.getEndY(), coord.getEndZ()).sub(position));
					
					for (int x = minModelSection.x(); x < maxModelSection.x(); x++) {
						for (int y = minModelSection.y(); x < maxModelSection.y(); y++) {
							for (int z = minModelSection.z(); x < maxModelSection.z(); z++) {
								updatingModelSections.add(new Vector3i(x, y, z));
							}
						}
					}
				}
			}
			
			for (Vector3i coord : updatingModelSections) {
				SizedBlockCollection section = modelCache.sectionAt(coord.x, coord.y, coord.z);
				world.addBlockCollection(section,
						coord.x * width + position.x(),
						coord.y * height + position.y(),
						coord.z * length + position.z(), true, shouldPlaceAir(), this);
			}
		}
		
		return true;
	}
	
	/**
	 * Check if this block entity overlaps a certian volume. <br>
	 * <b>Note:</b> <code>WorldChunked</code> only checks if any of its sections
	 * overlap the volume, not the blocks themselves.
	 */
	public boolean overlapsVolume(Vector3dc point1, Vector3dc point2) {
		if (modelCache == null) return false;
		
		Vector3ic section1 = modelCache.getSection(MathUtils.floorVector(point1));
		Vector3ic section2 = modelCache.getSection(MathUtils.floorVector(point2));
		Set<Vector3ic> sections = modelCache.getSections();
		
		if (sections.contains(section1) || sections.contains(section2)) {
			return true;
		}
		// See if any of our sections are within the volume.
		for (Vector3ic section : sections) {
			if (section1.x() <= section.x() && section.x() <= section2.x()
					&& section1.y() <= section.y() && section.y() <= section2.y()
					&& section1.z() <= section.z() && section.z() <= section2.z()) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean overlapsArea(double[] point1, double[] point2) {
		return overlapsVolume(new Vector3d(point1[0], 0, point2[0]), new Vector3d(point2[0], Chunk.HEIGHT, point2[1]));
	}

	/**
	 * Identify whether the sections of the block collection are aligned with the
	 * sections of the world. Used for optimization.
	 */
	public boolean isAligned() {
		if (modelCache == null) return false;
		
		Vector3ic position = getBlockPosition();
		int sectionWidth = modelCache.getSectionWidth();
		int sectionHeight = modelCache.getSectionHeight();
		int sectionLength = modelCache.getSectionLength();
		return (position.x() % 16 == 0 && position.y() % 16 == 0 && position.z() % 16 == 0 && sectionWidth == 16
				&& sectionHeight == 16 && sectionLength == 16);
	}

	@Override
	public Set<SectionCoordinate> getOverlappingSections() {
		if (modelCache == null) return new HashSet<>();
		
		int sectionWidth = modelCache.getSectionWidth();
		int sectionHeight = modelCache.getSectionHeight();
		int sectionLength = modelCache.getSectionLength();
		Set<SectionCoordinate> overlapping = new HashSet<>();
		Vector3ic position = getBlockPosition();
		
		// If our sections are aligned, we can just return the sections from the collection.
		if (isAligned()) {
			for (Vector3ic coord : modelCache.getSections()) {
				overlapping.add(new SectionCoordinate(coord.add(getWorld().getSection(position), new Vector3i())));
			}
			return overlapping;
		}
		
		// Identify the world sections that the collection sections overlap with.
		for (Vector3ic coord : modelCache.getSections()) {
			int minX = coord.x() * sectionWidth + position.x();
			int minY = coord.y() * sectionHeight + position.y();
			int minZ = coord.z() * sectionLength + position.z();
			
			int maxX = minX + sectionWidth;
			int maxY = minY + sectionHeight;
			int maxZ = minZ + sectionLength;
			
			Vector3i minSection = getWorld().getSection(minX, minY, minZ);
			Vector3i maxSection = getWorld().getSection(maxX, maxY, maxZ);
			
			for (int x = minSection.x; x < maxSection.x; x++) {
				for (int y = minSection.y; y < maxSection.y; y++) {
					for (int z = minSection.z; z < maxSection.z; z++) {
						overlapping.add(new SectionCoordinate(x, y, z));
					}
				}
			}
		}
		
		return overlapping;
	}

	@Override
	public Block blockAt(Vector3ic coord) {
		Vector3i local = coord.sub(getBlockPosition(), new Vector3i());
		return modelCache.blockAt(local);
	}
	
	protected boolean shouldPlaceAir() {
		return ((BooleanAttribute) getAttribute("place_air")).getValue();
	}

	@Override
	public Vector3ic[] getBounds() {
		return boundsCache;
	}

	@Override
	protected boolean needsRecompiling() {
		return true;
	}

	@Override
	public void updateBlocks() {
		if (!((StringAttribute) getAttribute("model")).getValue().equals(modelpath)) {
			reload();	
		};
	}

	@Override
	public BlockCollection getBlockCollection() {
		return modelCache;
	}
}
