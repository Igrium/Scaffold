package org.scaffoldeditor.scaffold.mc;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.python.google.common.base.Splitter;

import com.github.mryurihi.tbnbt.TagType;
import com.github.mryurihi.tbnbt.tag.NBTTag;
import com.github.mryurihi.tbnbt.tag.NBTTagCompound;

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
	public static String parseBlockState(NBTTagCompound blockState, JSONObject file) {
		if (file.has("variants")) {
			return parseVariants(blockState, file);
		} else if (file.has("multipart")) {
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
	public static String parseVariants(NBTTagCompound blockState, JSONObject file) {
		JSONObject variants = file.getJSONObject("variants");
		
		for (String key : variants.keySet()) {
			if (shouldUseVariant(blockState, key)) {
				JSONObject varObj = variants.optJSONObject(key);
				if (varObj != null) {
					return parseVariant(varObj);
				} else {
					return parseVariant(variants.getJSONArray(key));
				}
			}
		}
		
		throw new IllegalArgumentException("Current block state, "+blockState.toString()+" is not listed in blockstate json file.");
	}
	
	protected static boolean shouldUseVariant(NBTTagCompound blockState, String varient) {
		// Parse the varient.
		Map<String, String> kvPairs = Splitter.on(',').withKeyValueSeparator("=").split(varient);
		
		for (String key : kvPairs.keySet()) {
			if (blockState.containsKey(key) && tagEquals(blockState.get(key), kvPairs.get(key))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Look through a varient JSON entry and decide which model to use.
	 * @param variant Varient JSON entry.
	 * @return Resourcepack path to block to use.
	 */
	protected static String parseVariant(JSONArray variant) {
		int index = (int) Math.random() * variant.length();
		
		return parseVariant(variant.getJSONObject(index));
	}
	
	/**
	 * Look through a varient JSON entry and decide which model to use.
	 * @param variant Varient JSON entry.
	 * @return Resourcepack path to block to use.
	 */
	protected static String parseVariant(JSONObject variant) {
		return variant.getString("model");
	}
	
	/**
	 * Parse a Minecraft block state with a multipart tag.
	 * @param blockState The block's current block state.
	 * @param file The block's blockstate.json file.
	 * @return Resourcepack path to the model that should be loaded.
	 */
	public static String parseMultipart(NBTTagCompound blockState, JSONObject file) {
		return null;
	}
	
	private static boolean tagEquals(NBTTag tag, String string) {
		if (tag.getTagType() == TagType.BYTE) {
			boolean bool = Boolean.parseBoolean(string);
			byte byt = tag.getAsTagByte().getValue();
			
			return ((bool && byt == 1) || (!bool && byt != 1));
		}
		
		if (tag.getTagType() == TagType.SHORT) {
			return (tag.getAsTagShort().getValue() == Short.valueOf(string));
		}
		
		if (tag.getTagType() == TagType.INT) {
			return (tag.getAsTagInt().getValue() == Integer.valueOf(string));
		}
		
		if (tag.getTagType() == TagType.LONG) {
			return (tag.getAsTagLong().getValue() == Long.valueOf(string));
		}
		
		if (tag.getTagType() == TagType.FLOAT) {
			return (tag.getAsTagFloat().getValue() == Float.valueOf(string));
		}
		
		if (tag.getTagType() == TagType.DOUBLE) {
			return (tag.getAsTagDouble().getValue() == Double.valueOf(string));
		}
		
		if (tag.getTagType() == TagType.STRING) {
			return (tag.getAsTagString().getValue().matches(string));
		}
		
		return false;
	}
	
}
