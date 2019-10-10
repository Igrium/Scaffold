package org.metaversemedia.scaffold.nbt;

import java.util.Iterator;
import java.util.List;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.DoubleTag;
import com.flowpowered.nbt.FloatTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.LongTag;
import com.flowpowered.nbt.ShortTag;
import com.flowpowered.nbt.StringTag;
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
			finalString = finalString+tag.getName()+":"+tagToString(tag);
			if (tagIterator.hasNext()) {
				finalString = finalString+',';
			}
		}
		
		return finalString+'}';
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
			tagString = doubleToString(doubleTag.getValue());
		} else if (tag.getType() == TagType.TAG_FLOAT) {
			FloatTag floatTag = (FloatTag) tag;
			tagString = floatToString(floatTag.getValue());
		} else if (tag.getType() == TagType.TAG_INT) {
			IntTag intTag = (IntTag) tag;
			tagString = intToString(intTag.getValue());
		} else if (tag.getType() == TagType.TAG_LIST) {
			ListTag<?> listTag = (ListTag<?>) tag;
			tagString = listToString(listTag.getValue());
		} else if (tag.getType() == TagType.TAG_LONG) {
			LongTag longTag = (LongTag) tag;
			tagString = longToString(longTag.getValue());
		} else if (tag.getType() == TagType.TAG_SHORT) {
			ShortTag shortTag = (ShortTag) tag;
			tagString = shortToString(shortTag.getValue());
		} else if (tag.getType() == TagType.TAG_STRING) {
			StringTag stringTag = (StringTag) tag;
			tagString = formatString(stringTag.getValue());
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
	
	private static String listToString(List<? extends Tag<?>> in) {
		String listString = "[";
		Iterator<? extends Tag<?>> listIterator = in.iterator();
		
		// Add all elements of list
		while (listIterator.hasNext()) {
			Tag<?> tag = listIterator.next();
			
			listString = listString+tagToString(tag);
			if (listIterator.hasNext())  {
				listString = listString+',';
			}
		}

		listString = listString+']';
		return listString;
	}
	
	private static String longToString(Long in) {
		return in.toString()+'l';
	}
	
	private static String shortToString(Short in) {
		return in.toString();
	}
	
	private static String formatString(String in) {
		// Escape quotes
		for (int i = 1; i < in.length(); i++) {
			if (in.charAt(i) == '"') {
				in = in.substring(0,i)+'\\'+in.substring(i); // insert \
				
				/* String is now a character longer,
				 * so we need to skip a character,
				 * which will be equal to the current char at i.
				 */
				i++; 
			}
		}
		
		return "\""+in+"\"";
	}
}
