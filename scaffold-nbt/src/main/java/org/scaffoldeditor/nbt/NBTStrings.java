package org.scaffoldeditor.nbt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mryurihi.tbnbt.TagType;
import mryurihi.tbnbt.tag.NBTTag;
import mryurihi.tbnbt.tag.NBTTagByte;
import mryurihi.tbnbt.tag.NBTTagCompound;
import mryurihi.tbnbt.tag.NBTTagDouble;
import mryurihi.tbnbt.tag.NBTTagFloat;
import mryurihi.tbnbt.tag.NBTTagInt;
import mryurihi.tbnbt.tag.NBTTagList;
import mryurihi.tbnbt.tag.NBTTagLong;
import mryurihi.tbnbt.tag.NBTTagString;

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
	public static String nbtToString(NBTTagCompound nbt) {
		if (nbt == null) {
			return "";
		}
		String finalString = "{";
		
		Map<String, NBTTag> tags = nbt.getValue();
		Iterator<String> iterator = tags.keySet().iterator();
		
		while (iterator.hasNext()) {
			String name = iterator.next();
			NBTTag tag = tags.get(name);
			finalString += name+":"+tagToString(tag);
			
			if (iterator.hasNext()) {
				finalString += ',';
			}
		}
		
		return finalString+'}';
	}
	
	/**
	 * Convert a tag to a string.
	 * @return String tag.
	 */
	public static String tagToString(NBTTag tag) {
		String tagString = null;
		
		if (tag.getTagType() == TagType.BYTE) {
			tagString = byteToString(tag.getAsTagByte().getValue());
		} else if (tag.getTagType() == TagType.BYTE_ARRAY) {
			tagString = byteArrayToString(tag.getAsTagByteArray().getValue());
		} else if (tag.getTagType() == TagType.COMPOUND) {
			tagString = nbtToString(tag.getAsTagCompound());
		} else if (tag.getTagType() == TagType.DOUBLE) {
			tagString = doubleToString(tag.getAsTagDouble().getValue());
		} else if (tag.getTagType() == TagType.FLOAT) {
			tagString = floatToString(tag.getAsTagFloat().getValue());
		} else if (tag.getTagType() == TagType.INT) {
			tagString = intToString(tag.getAsTagInt().getValue());
		} else if (tag.getTagType() == TagType.INT_ARRAY) {
			// TODO implement this.
		} else if (tag.getTagType() == TagType.LIST) {
			tagString = listToString(tag.getAsTagList().getValue());
		} else if (tag.getTagType() == TagType.LONG) {
			tagString = longToString(tag.getAsTagLong().getValue());
		} else if (tag.getTagType() == TagType.LONG_ARRAY) {
			// TODO implement this.
		} else if (tag.getTagType() == TagType.SHORT) {
			tagString = shortToString(tag.getAsTagShort().getValue());
		} else if (tag.getTagType() == TagType.STRING) {
			tagString = formatString(tag.getAsTagString().getValue());
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
	
	private static String listToString(List<NBTTag> in) {
		String listString = "[";
		Iterator<? extends NBTTag> listIterator = in.iterator();
		
		// Add all elements of list
		while (listIterator.hasNext()) {
			NBTTag tag = listIterator.next();
			
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
	public static NBTTagCompound nbtFromString(String inString) throws IOException {
		NBTTagCompound map = new NBTTagCompound(new HashMap<String, NBTTag>());
		
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
			
			// Break up into name and value
			String[] keyValuePair = splitString(s, ':');
			String name;
			String value;
			if (keyValuePair.length == 2) {
				name = keyValuePair[0];
				value = keyValuePair[1];
			} else {
				name = "";
				value = s;
			}
			map.put(name, parseTag(value));
		}

		return map;
	}
	
	
	/**
	 * Parse a string formatted tag back into a tag.
	 * @param in String to parse.
	 * @return Parsed tag.
	 * @throws IOException If the string is formatted improperly.
	 */
	public static NBTTag parseTag(String value) throws IOException {

		// Generate tag
		NBTTag tag = null;
		if (value.charAt(0) == '{') {
			tag = nbtFromString(value);
		} else if (isInteger(value)) {
			tag = new NBTTagInt(Integer.parseInt(value));
		} else if (isNumber(value)) {
			if (value.charAt(value.length()-1) == 'l') {
				tag = new NBTTagLong(Long.parseLong(value)); // Check for long
			}
			else if (value.charAt(value.length()-1) == 'f' ||
					value.charAt(value.length()-1) == 'F') { // Check for float
				tag = new NBTTagFloat(Float.parseFloat(value));
			} else {
				tag = new NBTTagDouble(Double.parseDouble(value));
			}
		} else if (value.charAt(0) == '[') {
			tag = parseList(value);
		} else if (value.charAt(0) == '"') {
			tag = new NBTTagString(parseString(value));
		} else if (value.charAt(value.length()-1) == 'b') {
			tag = new NBTTagByte(parseByte(value));
		} else {
			throw new IOException("Unable to parse nbt string: "+value);
		}
		
		return tag;
	}
	
	private static NBTTagList parseList(String inString) throws IOException {
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
		
		List<NBTTag> tags = new ArrayList<NBTTag>();
		for (String s : stringTags) {
			tags.add(parseTag(s));
		}
		
		return new NBTTagList(tags);
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
