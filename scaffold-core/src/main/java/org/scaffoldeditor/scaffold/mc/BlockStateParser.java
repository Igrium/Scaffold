package org.scaffoldeditor.scaffold.mc;

import java.nio.file.Path;
import java.nio.file.Paths;
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
	 * Contains data about a block state obtained by the block state file.
	 * @author Igrium
	 */
	public static class BlockStateData {
		public String model;
		public int x = 0;
		public int y = 0;
		public boolean uvLock = false;
		public float weight = 1;
	}
	
	/**
	 * Locate the blockstate file that a block should use.
	 * @param name Namespaced name of block.
	 * @return Scaffold path to blockstate file.
	 */
	public static Path locateBlockState(String name) {
		String[] nameSeperated = name.split(":");
		return Paths.get("assets", nameSeperated[0], "blockstates", nameSeperated[2]+".json");
	}
	
	/**
	 * Parse a Minecraft block state.
	 * @param blockState The block's current block state.
	 * @param file The block's blockstate.json file.
	 * @return Model data of the model that should be loaded.
	 */
	public BlockStateData parseBlockState(NBTTagCompound blockState, JSONObject file) {
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
	 * @return Model data of the model that should be loaded.
	 */
	public BlockStateData parseVariants(NBTTagCompound blockState, JSONObject file) {
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
	
	protected boolean shouldUseVariant(NBTTagCompound blockState, String varient) {
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
	 * @return Model data
	 */
	protected BlockStateData parseVariant(JSONArray variant) {
		float[] weights = new float[variant.length()];
		for (int i = 0; i < weights.length; i++) {
			JSONObject entry = variant.getJSONObject(i);
			weights[i] = entry.has("weight") ? entry.getFloat("weight") : 1;
		}
		int index = chooseIndex(weights);
		return parseVariant(variant.getJSONObject(index));
	}
	
	/**
	 * Look through a varient JSON entry and decide which model to use.
	 * @param variant Varient JSON entry.
	 * @return Model data.
	 */
	protected BlockStateData parseVariant(JSONObject variant) {
		BlockStateData data = new BlockStateData();
		data.model = variant.getString("model");
		if (variant.has("x")) data.x = variant.getInt("x");
		if (variant.has("y")) data.y = variant.getInt("y");
		if (variant.has("uvlock")) data.uvLock = variant.getBoolean("uvlock");
		if (variant.has("weight")) data.weight = variant.getFloat("weight");
		
		return data;
	}
	
	/**
	 * Parse a Minecraft block state with a multipart tag.
	 * @param blockState The block's current block state.
	 * @param file The block's blockstate.json file.
	 * @return Model data of the model that should be loaded.
	 */
	public BlockStateData parseMultipart(NBTTagCompound blockState, JSONObject file) {
		return null;
	}
	
	/**
	 * Check if a tag value equals a string value.
	 * @param tag The tag value.
	 * @param string The string value.
	 * @return If they equal each other.
	 */
	protected static boolean tagEquals(NBTTag tag, String string) {
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
	
	private static int chooseIndex(float[] weights) {
		float sum = 0;
		for (float val : weights) sum += val;
		
		float randNum = (float) (Math.random() * sum);
		
		sum = 0;
		for (int i = 0; i < weights.length; i++) {
			sum += weights[i];
			if (randNum < sum) return i;
		}
		return weights.length - 1;
	}
}
