package org.metaversemedia.scaffold.nbt;

import java.util.Iterator;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
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
			
			String tagValue = null;
			if (tag.getType() == TagType.TAG_BYTE_ARRAY) {
			}
		}
		
		return finalString;
	}
	
	public static String byteArrayToString(byte[] byteArray) {
		return new String(byteArray);
	}
}
