package org.scaffoldeditor.scaffold.level.entity.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.AssetAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.render.BlockRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

public class BlockModelTest extends Entity {
	
	public static void register() {
		EntityRegistry.registry.put("test_blockmodel", BlockModelTest::new);
	}

	public BlockModelTest(Level level, String name) {
		super(level, name);
	}
	
	private String oldModelPath = "";
	private BlockCollection model = null;

	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> def = new HashMap<>();
		def.put("model", new AssetAttribute("schematic", ""));
		return def;
	}
	
	public String getModelPath() {
		return (String) getAttribute("model").getValue();
	}
	
	public AssetAttribute getModelAttribute() {
		return (AssetAttribute) getAttribute("model");
	}
	
	public void reload() {
		try {
			if (getModelPath().length() == 0) model = null;
			else
				model = (BlockCollection) getLevel().getProject().assetManager().loadAsset(getModelPath(), false);
		} catch (IOException e) {
			LogManager.getLogger().error("Unable to load asset: "+getModelPath(), e);
		}
	}
	
	@Override
	public void onUpdateAttributes(boolean noRecompile) {
		super.onUpdateAttributes(noRecompile);
		if (!getModelPath().equals(oldModelPath)) {
			reload();
		}
	}
	
	@Override
	public Set<RenderEntity> getRenderEntities() {
		Set<RenderEntity> set = super.getRenderEntities();
		if (model == null) return set;
		set.add(new BlockRenderEntity(this, model, getPreviewPosition(), new Vector3f(0, 0, 0), "model"));
		return set;
	}
}
