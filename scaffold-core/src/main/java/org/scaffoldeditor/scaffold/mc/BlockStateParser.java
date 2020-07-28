package org.scaffoldeditor.scaffold.mc;

import org.json.JSONObject;

/**
 * Handles the parsing of Minecraft block states.
 * @author Igrium
 * @see <a href=https://minecraft.gamepedia.com/Model#Block_states>Block States</a>
 */
public class BlockStateParser {
	
	/**
	 * Parse a Minecraft block state.
	 * @param blockState The block's current block state.
	 * @param file The block's blockstate.json file.
	 * @return Resourcepack path to the model that should be loaded.
	 */
	public static String parseBlockState(JSONObject blockState, JSONObject file) {
		if (blockState.has("variants")) {
			return parseVariants(blockState, file);
		} else if (blockState.has("multipart")) {
			return parseMultipart(blockState, file);
		} else {
			throw new IllegalArgumentException("Each block state file must have either a 'variants' or a 'multipart' tag.");
		}
	}
	
	/**
	 * Parse a Minecraft block state with a variants tag.
	 * @param blockState The block's current block state.
	 * @param file The block's blockstate.json file.
	 * @return Resourcepack path to the model that should be loaded.
	 */
	public static String parseVariants(JSONObject blockState, JSONObject file) {
		return null;
	}
	
	/**
	 * Parse a Minecraft block state with a multipart tag.
	 * @param blockState The block's current block state.
	 * @param file The block's blockstate.json file.
	 * @return Resourcepack path to the model that should be loaded.
	 */
	public static String parseMultipart(JSONObject blockState, JSONObject file) {
		return null;
	}
}
