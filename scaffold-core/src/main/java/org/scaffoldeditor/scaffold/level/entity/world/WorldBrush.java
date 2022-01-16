package org.scaffoldeditor.scaffold.level.entity.world;

import java.util.Map;
import java.util.Set;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.GenericBlockCollection;
import org.scaffoldeditor.nbt.block.WorldMath.SectionCoordinate;
import org.scaffoldeditor.nbt.math.MathUtils;
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
		Vector3ic endLocal = MathUtils.floorVector(getEndPoint());
		for (int x = 0; x < endLocal.x(); x++) {
			for (int y = 0; y < endLocal.y(); y++) {
				for (int z = 0; z < endLocal.z(); z++) {
					Vector3i blockPos = new Vector3i(x,y,z).add(getBlockPosition());
					world.setBlock(blockPos.x, blockPos.y, blockPos.z,
							blockAt(blockPos), this);
				}
			}
		}
		
		return false;
	}

	@Override
	public Block blockAt(Vector3ic coord) {
		BlockTexture texture = getTexture();
		Vector3dc scale;
		if (texture.supportsScaling()) {
			scale = getTextureScale();
		} else {
			scale = new Vector3d(1,1,1);
		}
		
		Vector3dc transformCoord = new Vector3d(coord).add(getTextureOffset());
		return getTexture().blockAt(transformCoord.x() / scale.x(), transformCoord.y() / scale.y(), transformCoord.z() / scale.z());
	}

	@Override
	public Vector3dc[] getBrushBounds() {
		Vector3dc position = getPosition();
		return new Vector3dc[] { position, position.add(getEndPoint(), new Vector3d()) };
	}
	
	@Override
	public Vector3ic[] getBounds() {
		Vector3ic position = getBlockPosition();
		return new Vector3ic[] { position, position.add(MathUtils.floorVector(getEndPoint()), new Vector3i()) };
	}
	
	/**
	 * Get the end point of this brush relative to it's start point. (AKA the root position)
	 */
	public Vector3dc getEndPoint() {
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
	
	public Vector3dc getTextureScale() {
		return textureScale.getValue();
	}
	
	public Vector3dc getTextureOffset() {
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
	public void setBrushBounds(Vector3dc[] newBounds, boolean suppressUpdate) {
		Map<String, Attribute<?>> att = Map.of(
			"position", new VectorAttribute(newBounds[0]),
			"end_point", new VectorAttribute(newBounds[1].sub(newBounds[0], new Vector3d()))
		);
		setAttributes(att);
	}
	
	@Override
	public BlockCollection getBlockCollection() {
		GenericBlockCollection blocks = new GenericBlockCollection();
		Vector3ic endPoint = MathUtils.floorVector(getEndPoint());
		for (int x = 0; x < endPoint.x(); x++) {
			for (int y = 0; y < endPoint.y(); y++) {
				for (int z = 0; z < endPoint.z(); z++) {
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
