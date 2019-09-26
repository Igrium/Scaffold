package org.metaversemedia.scaffold.nbt;

import java.util.Map;

/**
 * Handles some constants for NBT files
 * @author Sam54123
 */
public final class Constants {
	/**
	 * Default block ids for if schematic doesn't specify them
	 */
	public static Map<Integer, String> DEFAULT_IDS;
	static {
		DEFAULT_IDS.put(0, "minecraft:air");
		DEFAULT_IDS.put(1, "minecraft:stone");
		DEFAULT_IDS.put(2, "minecraft:dirt");
		DEFAULT_IDS.put(4, "minecraft:cobblestone");
		DEFAULT_IDS.put(5, "minecraft:planks");
		DEFAULT_IDS.put(6, "minecraft:sapling");
		DEFAULT_IDS.put(7, "minecraft:bedrock");
		DEFAULT_IDS.put(8, "minecraft:flowing_water");
		DEFAULT_IDS.put(9, "minecraft:water");
		DEFAULT_IDS.put(10, "minecraft:flowing_lava");
		DEFAULT_IDS.put(11, "minecraft:lava");
		DEFAULT_IDS.put(12, "minecraft:sand");
		DEFAULT_IDS.put(13, "minecraft:gravel");
		DEFAULT_IDS.put(14, "minecraft:gold_ore");
		DEFAULT_IDS.put(15, "minecraft:iron_ore");
		DEFAULT_IDS.put(16, "minecraft:coal_ore");
		DEFAULT_IDS.put(17, "minecraft:log");
		DEFAULT_IDS.put(18, "minecraft:leaves");
		DEFAULT_IDS.put(19, "minecraft:sponge");
		DEFAULT_IDS.put(20, "minecraft:glass");
	}
}
