package org.scaffoldeditor.editor.editor3d.block;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.scaffoldeditor.editor.editor3d.EditorApp;
import org.scaffoldeditor.editor.editor3d.util.EditorUtils;
import org.scaffoldeditor.scaffold.util.JSONUtils;

import com.rvandoosselaer.blocks.Block;
import com.rvandoosselaer.blocks.ShapeIds;
import com.rvandoosselaer.blocks.TypeIds;

/**
 * Manages the loading and saving of JME Block objects.
 * <br>
 * Each blockmodel is a separate block.
 * @author Igrium
 */
@SuppressWarnings("unused")
public class BlockManager {
	
	private Map<String, Block> registry = new HashMap<String, Block>();
	
	
	/**
	 * Check if the registry already contains a block.
	 * @param key JME path to block's model file.
	 * @return Has the block?
	 */
	public boolean has(String key) {
		return registry.containsKey(key);
	}
	
	/**
	 * Get a block from the registry, or generate it if it doesn't exist.
	 * @param key JME path to block's model file.
	 * @return JME block.
	 */
	public Block get(String key) {
		Block block = registry.get(key);
		
		if (block == null) {
			return generateBlock(key);
		}
		
		return block;
	}
	
	/**
	 * Generate a block from a model file and add it to the registry.
	 * @param key JME path to block's model file.
	 * @return Generated block.
	 */
	protected Block generateBlock(String key) {
		// Block texture from JSON. Also pre-loads the JSON into memory.
		// TODO: support multiple textures.
		
		String typeID;
		
		try {
			JSONObject modelJson = EditorUtils.loadJMEJson(key);
			JSONObject textures = modelJson.optJSONObject("textures");
			String texString = textures.getString((String) textures.keySet().toArray()[0]); // TODO: make this better
			String texMCPath = "textures/"+texString+".png";
			
			typeID = Paths.get(EditorApp.getInstance().getParent().getProject()
					.assetManager().getNamespace(texMCPath), texMCPath).toString();
			
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			typeID = TypeIds.COBBLESTONE;
		}
		
		Block block = Block.builder()
                .name(key)
                .shape(key)
                .type(typeID)
                .usingMultipleImages(false)
                .transparent(false)
                .solid(true)
                .build();
		registry.put(key, block);
		
		return block;
	}
}
