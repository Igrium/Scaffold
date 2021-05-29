package org.scaffoldeditor.nbt;

import java.io.IOException;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

/**
 * Utility class for common nbt functions
 * @author Sam54123
 * @deprecated use {@link SNBTUtil} instead.
 */
public class NBTStrings {
	/**
	 * Convert nbt to a string.
	 * @param nbt NBT input.
	 * @return Generated string.
	 * @deprecated Use {@link SNBTUtil#toSNBT} instead.
	 */
	public static String nbtToString(CompoundTag nbt) {
		try {
			return SNBTUtil.toSNBT(nbt);
		} catch (IOException e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	/**
	 * Convert a tag to a string.
	 * @return String tag.
	 * @deprecated use {@link SNBTUtil#toSNBT} instead.
	 */
	public static String tagToString(Tag<?> tag) {
		try {
			return SNBTUtil.toSNBT(tag);
		} catch (IOException e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	/**
	 * Generate a compound map from a string.
	 * @param inString String to generate from.
	 * @return Generated CompoundMap.
	 * @throws IllegalArgumentException If the string is formatted improperly.
	 * @deprecated Use {@link SNBTUtil#fromSNBT} instead.
	 */
	public static CompoundTag nbtFromString(String inString) throws IllegalArgumentException {		
		try {
			return (CompoundTag) SNBTUtil.fromSNBT(inString);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Parse a string formatted tag back into a tag.
	 * @param inString String to parse.
	 * @return Parsed tag.
	 * @throws IllegalArgumentException If the string is formatted improperly.
	 * @deprecated use {@link SNBTUtil#fromSNBT} instead.
	 */
	public static Tag<?> parseTag(String inString) throws IllegalArgumentException {
		try {
			return SNBTUtil.fromSNBT(inString);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
}
