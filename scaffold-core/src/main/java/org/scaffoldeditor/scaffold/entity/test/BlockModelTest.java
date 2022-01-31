package org.scaffoldeditor.scaffold.entity.test;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.joml.Vector3d;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.attribute.AssetAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.render.BlockRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

public class BlockModelTest extends Entity {
	
	public static void register() {
		EntityRegistry.registry.put("test_blockmodel", BlockModelTest::new);
	}

	public BlockModelTest(Level level, String name) {
		super(level, name);
	}
	
	private BlockCollection modelCache = null;

	@Attrib
	protected AssetAttribute model = new AssetAttribute("schematic", "");
	
	public String getModelPath() {
		return model.getValue();
	}
	
	public AssetAttribute getModelAttribute() {
		return model;
	}
	
	public void reload() {
		try {
			if (getModelPath().length() == 0) modelCache = null;
			else
				modelCache = (BlockCollection) getLevel().getProject().assetManager().loadAsset(getModelPath(), false);
		} catch (IOException e) {
			LogManager.getLogger().error("Unable to load asset: "+getModelPath(), e);
		}
	}

	@Override
	protected void onSetAttributes(Map<String, Attribute<?>> updated) {
		super.onSetAttributes(updated);
		if (updated.containsKey("model")) reload();
	}
	
	@Override
	public Set<RenderEntity> getRenderEntities() {
		Set<RenderEntity> set = super.getRenderEntities();
		if (modelCache == null) return set;
		set.add(new BlockRenderEntity(this, modelCache, getPreviewPosition(), new Vector3d(), "model"));
		return set;
	}
}
