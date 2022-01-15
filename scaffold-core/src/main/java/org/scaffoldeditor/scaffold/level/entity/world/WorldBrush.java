package org.scaffoldeditor.scaffold.level.entity.world;

import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.GenericBlockCollection;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.math.Vector3d;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.block_textures.BlockTexture;
import org.scaffoldeditor.scaffold.block_textures.SingleBlockTexture;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BrushEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockTextureAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

public class WorldBrush extends BaseBlockEntity implements BrushEntity {
	
	public static String REGISTRY_NAME = "world_brush";
	
	public static void register() {
		EntityRegistry.registry.put(REGISTRY_NAME, new EntityFactory<Entity>() {
			@Override
			public Entity create(Level level, String name) {
				return new WorldBrush(level, name);
			}
		});
	}

	@Attrib(name = "end_point")
	VectorAttribute endPoint = new VectorAttribute(4, 4, 4);

	@Attrib
	BlockTextureAttribute texture = new BlockTextureAttribute(new SingleBlockTexture(new Block("minecraft:stone")));

	@Attrib(name = "texture_scale")
	VectorAttribute textureScale = new VectorAttribute(1, 1, 1);

	@Attrib(name = "texture_offset")
	VectorAttribute textureOffset = new VectorAttribute(0, 0, 0);

	public WorldBrush(Level level, String name) {
		super(level, name);
	}


	@Override
	public boolean compileWorld(BlockWorld world, boolean full, Set<SectionCoordinate> sections) {
		reloadTexture();
		Vector3i endLocal = getEndPoint().floor();
		for (int x = 0; x < endLocal.x; x++) {
			for (int y = 0; y < endLocal.y; y++) {
				for (int z = 0; z < endLocal.z; z++) {
					Vector3i blockPos = getBlockPosition().add(new Vector3i(x,y,z));
					world.setBlock(blockPos.x, blockPos.y, blockPos.z,
							blockAt(blockPos), this);
				}
			}
		}
		
		return false;
	}

	@Override
	public Block blockAt(Vector3i coord) {
		BlockTexture texture = getTexture();
		Vector3d scale = new Vector3d(1,1,1);
		if (texture.supportsScaling()) {
			scale = getTextureScale().toDouble();
		}
		
		Vector3f transformCoord = coord.toFloat().add(getTextureOffset());
		return getTexture().blockAt(transformCoord.x / scale.x, transformCoord.y / scale.y, transformCoord.z / scale.z);
	}

	@Override
	public Vector3f[] getBrushBounds() {
		Vector3f position = getPosition();
		return new Vector3f[] { position, position.add(getEndPoint()) };
	}
	
	@Override
	public Vector3i[] getBounds() {
		Vector3i position = getPosition().floor();
		return new Vector3i[] { position, position.add(getEndPoint().floor()) };
	}
	
	/**
	 * Get the end point of this brush relative to it's start point. (AKA the root position)
	 */
	public Vector3f getEndPoint() {
		return endPoint.getValue();
	}
	
	/**
	 * Get the block texture which this brush uses to choose its blocks.
	 */
	public BlockTexture getTexture() {
		return texture.getValue();
	}
	
	public void reloadTexture() {
		texture.reload();
	}
	
	public Vector3f getTextureScale() {
		return textureScale.getValue();
	}
	
	public Vector3f getTextureOffset() {
		return textureOffset.getValue();
	}

	@Override
	protected boolean needsRecompiling() {
		return true;
	}

	@Override
	public void updateBlocks() {
	}

	@Override
	public void setBrushBounds(Vector3f[] newBounds, boolean suppressUpdate) {
		Map<String, Attribute<?>> att = Map.of(
			"position", new VectorAttribute(newBounds[0]),
			"end_point", new VectorAttribute(newBounds[1].subtract(newBounds[0]))
		);
		setAttributes(att);
	}
	
	@Override
	public BlockCollection getBlockCollection() {
		GenericBlockCollection blocks = new GenericBlockCollection();
		Vector3i endPoint = getEndPoint().floor();
		for (int x = 0; x < endPoint.x; x++) {
			for (int y = 0; y < endPoint.y; y++) {
				for (int z = 0; z < endPoint.z; z++) {
					Block block = blockAt(new Vector3i(x, y, z).add(getBlockPosition()));
					if (block != null) blocks.setBlock(x, y, z, block);
				}
			}
		}
		return blocks;
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/world_brush.sdoc", super.getDocumentation());
	}
}
