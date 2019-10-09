package org.metaversemedia.scaffold.nbt;

import java.util.Iterator;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.DoubleTag;
import com.flowpowered.nbt.FloatTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;

/**
 * Utility class for common nbt functions
 * @author Sam54123
 *
 */
public class NBTUtils {
	/**
	 * Convert nbt to a string.
	 * @param nbt NBT input.
	 * @return Generated string.
	 */
	public static String nbtToString(CompoundMap nbt) {
		if (nbt == null) {
			return "";
		}
		String finalString = "{";
		
		Iterator<Tag<?>> tagIterator = nbt.iterator();
		
		while (tagIterator.hasNext()) {
			Tag<?> tag = tagIterator.next();
			
		}
		
		return finalString;
	}
	
	/**
	 * Convert a tag to a string.
	 * @return String tag.
	 */
	public static String tagToString(Tag<?> tag) {
		String tagString = null;
		
		if (tag.getType() == TagType.TAG_BYTE_ARRAY) {
			ByteArrayTag byteArray = (ByteArrayTag) tag;
			tagString = byteArrayToString(byteArray.getValue());
		} else if (tag.getType() == TagType.TAG_COMPOUND) {
			CompoundTag compoundTag = (CompoundTag) tag;
			tagString = nbtToString(compoundTag.getValue());
		} else if (tag.getType() == TagType.TAG_DOUBLE) {
			DoubleTag doubleTag = (DoubleTag) tag;
			tagString = doubleToString(3.0d);
		} else if (tag.getType() == TagType.TAG_FLOAT) {
			FloatTag floatTag = (FloatTag) tag;
			tagString = floatToString(floatTag.getValue());
		} else if (tag.getType() == TagType.TAG_INT) {
			IntTag intTag = (IntTag) tag;
			tagString = intToString(intTag.getValue());
		}
		
		return tagString;
	}
	
	private static String byteArrayToString(byte[] byteArray) {
		return new String(byteArray);
	}
	
	private static String doubleToString(Double in) {
		return in.toString()+'d';
	}
	
	private static String floatToString(Float in) {
		return in.toString()+'f';
	}
	
	private static String intToString(Integer in) {
		return in.toString();
	}
}
