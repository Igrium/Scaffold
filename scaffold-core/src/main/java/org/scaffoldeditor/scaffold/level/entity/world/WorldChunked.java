package org.scaffoldeditor.scaffold.level.entity.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.block.ChunkedBlockCollection;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.scaffold.io.AssetLoaderRegistry;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;

/**
 * A block entity which is optimized for chunked block collections, 
 * potentially infinite in size.
 * 
 * @author Igrium
 */
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
	
	private ChunkedBlockCollection model;
	
	// Keep track of the model path on our own for optimization.
	private String modelpath;
	private Vector3i[] boundsCache = new Vector3i[] {new Vector3i(0,0,0), new Vector3i(0,0,0)};

	public WorldChunked(Level level, String name) {
		super(level, name);
	}
	
	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = new HashMap<>();
		map.put("model", new StringAttribute(""));
		map.put("place_air", new BooleanAttribute(false));
		return map;
	}
	
	/**
	 * Reload model from file.
	 */
	public void reload() {
		
		StringAttribute attribute = (StringAttribute) getAttribute("model");
		String model = attribute.getValue();
		LogManager.getLogger().info("Loading model " + model);
		modelpath = model;
		if (model.length() == 0) {
			this.model = null;
			return;
		}
		
		if (AssetLoaderRegistry.isTypeAssignableTo(FilenameUtils.getExtension(model), ChunkedBlockCollection.class)) {
			try {
//				this.model = Structure.fromCompoundMap((CompoundTag) NBTUtil.read(modelFile).getTag());
				this.model = (ChunkedBlockCollection) getProject().assetManager().loadAsset(model, false);
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
		if (model == null || model.getSections().size() == 0) return;
		
		Vector3i def = model.getSections().iterator().next();
		
		int minX = def.x;
		int minY = def.y;
		int minZ = def.z;
		
		int maxX = def.x;
		int maxY = def.y;
		int maxZ = def.z;
		
		int width = model.getSectionWidth();
		int height = model.getSectionHeight();
		int length = model.getSectionLength();
		
		for (Vector3i section : model.getSections()) {
			if (section.x < minX) minX = section.x;
			if (section.y < minY) minY = section.y;
			if (section.z < minZ) minZ = section.z;
			
			if (section.x > maxX) maxX = section.x;
			if (section.y > maxY) maxY = section.y;
			if (section.z > maxZ) maxZ = section.z;
		}
		
		boundsCache[0] = new Vector3i(minX * width, minY * height, minZ * length);
		boundsCache[1] = new Vector3i(maxX * width, maxY * height, maxZ * length);
	}

	@Override
	public boolean compileWorld(BlockWorld world, boolean full, Set<SectionCoordinate> worldSections) {
		if (full) reload();
		if (model == null) return false;
		
		int width = model.getSectionWidth();
		int length = model.getSectionLength();
		int height = model.getSectionHeight();
		Vector3i position = getBlockPosition();
		
		if (worldSections == null) {
			// Compile entire model
			for (Vector3i coord : model.getSections()) {
				SizedBlockCollection section = model.sectionAt(coord.x, coord.y, coord.z);
				world.addBlockCollection(section, coord.x * width + position.x, coord.y * height + position.y,
						coord.z * length + position.z, true, shouldPlaceAir(), this);
			}
			return true;
		} else {
			// Compile selection set
			Set<Vector3i> updatingModelSections = new HashSet<>();
			if (isAligned()) {
				for (SectionCoordinate coord : worldSections) {
					updatingModelSections
							.add(coord.subtract(world.getSection(position)));
				}
			} else {
				// Identify the collection sections that the world sections overlap with.
				for (SectionCoordinate coord : worldSections) {
					Vector3i minModelSection = model.getSection(new Vector3i(coord.getStartX(), coord.getStartY(), coord.getStartZ()).subtract(position));
					Vector3i maxModelSection = model.getSection(new Vector3i(coord.getEndX(), coord.getEndY(), coord.getEndZ()).subtract(position));
					
					for (int x = minModelSection.x; x < maxModelSection.x; x++) {
						for (int y = minModelSection.y; x < maxModelSection.y; y++) {
							for (int z = minModelSection.z; x < maxModelSection.z; z++) {
								updatingModelSections.add(new Vector3i(x, y, z));
							}
						}
					}
				}
			}
			
			for (Vector3i coord : updatingModelSections) {
				SizedBlockCollection section = model.sectionAt(coord.x, coord.y, coord.z);
				world.addBlockCollection(section,
						coord.x * width + position.x,
						coord.y * height + position.y,
						coord.z * length + position.z, true, shouldPlaceAir(), this);
			}
		}
		
		return true;
	}
	
	/**
	 * Check if this block entity overlaps a certian volume. <br>
	 * <b>Note:</b> <code>WorldChunked</code> only checks if any of its sections
	 * overlap the volume, not the blocks themselves.
	 */
	public boolean overlapsVolume(Vector3f point1, Vector3f point2) {
		if (model == null) return false;
		
		Vector3i section1 = model.getSection(point1.floor());
		Vector3i section2 = model.getSection(point2.floor());
		Set<Vector3i> sections = model.getSections();
		
		if (sections.contains(section1) || sections.contains(section2)) {
			return true;
		}
		// See if any of our sections are within the volume.
		for (Vector3i section : sections) {
			if (section1.x <= section.x && section.x <= section2.x
					&& section1.y <= section.y && section.y <= section2.y
					&& section1.z <= section.z && section.z <= section2.z) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean overlapsArea(float[] point1, float[] point2) {
		return overlapsVolume(new Vector3f(point1[0], 0, point2[0]), new Vector3f(point2[0], Chunk.HEIGHT, point2[1]));
	}

	/**
	 * Identify whether the sections of the block collection are aligned with the
	 * sections of the world. Used for optimization.
	 */
	public boolean isAligned() {
		if (model == null) return false;
		
		Vector3i position = getBlockPosition();
		int sectionWidth = model.getSectionWidth();
		int sectionHeight = model.getSectionHeight();
		int sectionLength = model.getSectionLength();
		return (position.x % 16 == 0 && position.y % 16 == 0 && position.z % 16 == 0 && sectionWidth == 16
				&& sectionHeight == 16 && sectionLength == 16);
	}

	@Override
	public Set<SectionCoordinate> getOverlappingSections() {
		if (model == null) return new HashSet<>();
		
		int sectionWidth = model.getSectionWidth();
		int sectionHeight = model.getSectionHeight();
		int sectionLength = model.getSectionLength();
		Set<SectionCoordinate> overlapping = new HashSet<>();
		Vector3i position = getPosition().floor();
		
		// If our sections are aligned, we can just return the sections from the collection.
		if (isAligned()) {
			for (Vector3i coord : model.getSections()) {
				overlapping.add(new SectionCoordinate(coord.add(getWorld().getSection(position))));
			}
			return overlapping;
		}
		
		// Identify the world sections that the collection sections overlap with.
		for (Vector3i coord : model.getSections()) {
			int minX = coord.x * sectionWidth + position.x;
			int minY = coord.y * sectionHeight + position.y;
			int minZ = coord.z * sectionLength + position.z;
			
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
	public Block blockAt(Vector3i coord) {
		Vector3i local = coord.subtract(getBlockPosition());
		return model.blockAt(local);
	}
	
	protected boolean shouldPlaceAir() {
		return ((BooleanAttribute) getAttribute("place_air")).getValue();
	}

	@Override
	public Vector3i[] getBounds() {
		return boundsCache;
	}

	@Override
	protected boolean needsRecompiling() {
		return true;
	}

	@Override
	public void onUpdateBlockAttributes() {
		if (!((StringAttribute) getAttribute("model")).getValue().equals(modelpath)) {
			reload();	
		};
	}
}
