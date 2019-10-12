package org.metaversemedia.scaffold.nbt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.ByteTag;
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
public class NBTStrings {
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
		
		if (tag.getType() == TagType.TAG_BYTE) {
			ByteTag byteTag = (ByteTag) tag;
			tagString = byteToString(byteTag.getValue());
		} else if (tag.getType() == TagType.TAG_BYTE_ARRAY) {
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
	
	private static String byteToString(byte inByte) {
		return Byte.toUnsignedInt(inByte)+"b";
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
		in = in.replace("\"", "\\\"");
		return "\""+in+"\"";
	}
	
	/**
	 * Generate a compound map from a string.
	 * @param inString String to generate from.
	 * @return Generated CompoundMap.
	 * @throws IOException If the string is formatted improperly.
	 */
	public static CompoundMap nbtFromString(String inString) throws IOException {
		CompoundMap map = new CompoundMap();
		
		if (inString.length() < 2) {
			return null;
		}
		
		// Remove newlines and whitespace
		inString = inString.trim();
		inString = inString.replace("\n", "");
		if (!(inString.charAt(0) == '{' && inString.charAt(inString.length()-1) == '}')) {
			throw new IOException("NBT String is missing brackets! ({})");
		}
		// Remove brackets
		inString = inString.substring(1,inString.length()-1);

		if (inString.length() < 1) {
			return map;
		}
		
		// Split string into tags
		String[] stringTags = splitString(inString, ',');
		for (String s : stringTags) {
			map.put(parseTag(s));
		}

		return map;
	}
	
	
	/**
	 * Parse a string formatted tag back into a tag.
	 * @param in String to parse.
	 * @return Parsed tag.
	 * @throws IOException If the string is formatted improperly.
	 */
	public static Tag<?> parseTag(String in) throws IOException {
		// Split string into name and value
		String name = null;
		String value = null;
		in = in.trim();
		
		String[] keyValuePair = splitString(in, ':');

		
		if (keyValuePair.length == 2) {
			name = keyValuePair[0];
			value = keyValuePair[1];
		} else {
			name = "";
			value = in;
		}
		
		

		// Generate tag
		Tag<?> tag = null;
		if (value.charAt(0) == '{') {
			tag = new CompoundTag(name, nbtFromString(value));
		} else if (isInteger(value)) {
			tag = new IntTag(name, Integer.parseInt(value));
		} else if (isNumber(value)) {
			if (value.charAt(value.length()-1) == 'l') {
				tag = new LongTag(name, Long.parseLong(value)); // Check for long
			}
			else if (value.charAt(value.length()-1) == 'f' ||
					value.charAt(value.length()-1) == 'F') { // Check for float
				tag = new FloatTag(name, Float.parseFloat(value));
			} else {
				tag = new DoubleTag(name, Double.parseDouble(value));
			}
		} else if (value.charAt(0) == '[') {
			tag = parseList(name, value);
		} else if (value.charAt(0) == '"') {
			tag = new StringTag(name, parseString(value));
		} else if (value.charAt(value.length()-1) == 'b') {
			tag = new ByteTag(name, parseByte(value));
		} else {
			throw new IOException("Unable to parse nbt string: "+in);
		}
		
		return tag;
	}
	
	private static ListTag<?> parseList(String name, String inString) throws IOException {
		// Remove newlines and whitespace
		inString = inString.trim();
		inString = inString.replace("\n", "");
		
		// Check for brackets
		if (!(inString.charAt(0) == '[' && inString.charAt(inString.length()-1) == ']')) {
			throw new IOException("NBT List String is missing brackets! ([])");
		}
		
		// Remove brackets
		inString = inString.substring(1, inString.length() - 1);

		// Split string into tags
		String[] stringTags = splitString(inString, ',');
		
		List<Tag<?>> tags = new ArrayList<Tag<?>>();
		for (String s : stringTags) {
			tags.add(parseTag(s));
		}
		
		return new ListTag(name, Tag.class, tags);
	}
	
	private static String parseString(String in) {
		in = in.replace("\\\"", "\""); // Replace '\"' with '"'
		return in.substring(1,in.length()-1); // Remove quotes
	}
	
	public static byte parseByte(String in) throws IOException {
		if (in.charAt(in.length()-1) != 'b') {
			throw new IOException("Unable to parse byte: "+in);
		}
		
		in = in.substring(0, in.length()-1); // remove b at the end
		int i2 = Integer.parseInt(in) & 0xFF;
		
		return (byte) i2;
	}
	
	/**
	 * Check if a string is parseable as an Integer.
	 * @param in String to check.
	 * @return Is parsable.
	 */
	private static boolean isInteger(String in) {
		try {
			Integer.parseInt(in);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Check if a string is parseable as a number.
	 * @param in String to check.
	 * @return Is parsable.
	 */
	private static boolean isNumber(String in) {
		try {
			Double.parseDouble(in);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Split a string into multiple strings.
	 * Breaks around seperator unless surrounded by [], {}, or "".
	 * @param in String to split.
	 * @parap seperator Character to use as seperator.
	 * @return Array of substrings.
	 */
	private static String[] splitString(String in, char seperator) {
		char[] charList = in.toCharArray();
		List<Integer> commas = new ArrayList<Integer>(); // Indexes to split string on
		
		for (int i = 0; i < charList.length; i++) { // Find commas in string
			if (charList[i] == '"') {
				i++;
				while (!(charList[i] == '"' && (i == 0 || charList[i-1] != '\\'))) {
					i++; // Skip through entire substring.
				}
				
			} else if (charList[i] == '{') {
				while (charList[i] != '}') {
					i++; // Skip through entire compound map.
				}
				
			} else if (charList[i] == '[') {
				while (charList[i] != ']') {
					i++; // Skip through entire array.
				}
			}
			if (charList[i] == seperator) {
				commas.add(i);
			}
		}
		
		if (commas.size() == 0) {
			return new String[] {in};
		}
		
		List<String> finalStrings = new ArrayList<String>();
		for (int i = 0; i < commas.size(); i++) {
			if (i == 0) {
				finalStrings.add(in.substring(0,commas.get(i)));
			} else {
				finalStrings.add(in.substring(commas.get(i-1)+1, commas.get(i)));	
			}
			
			if (i == commas.size()-1) { // Make sure to add last string.
				finalStrings.add(in.substring(commas.get(i)+1));
			}
		}
		
		return finalStrings.toArray(new String[0]);
	}
}
