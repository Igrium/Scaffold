package org.scaffoldeditor.nbt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.mryurihi.tbnbt.TagType;
import com.github.mryurihi.tbnbt.tag.NBTTag;
import com.github.mryurihi.tbnbt.tag.NBTTagByte;
import com.github.mryurihi.tbnbt.tag.NBTTagCompound;
import com.github.mryurihi.tbnbt.tag.NBTTagDouble;
import com.github.mryurihi.tbnbt.tag.NBTTagFloat;
import com.github.mryurihi.tbnbt.tag.NBTTagInt;
import com.github.mryurihi.tbnbt.tag.NBTTagList;
import com.github.mryurihi.tbnbt.tag.NBTTagLong;
import com.github.mryurihi.tbnbt.tag.NBTTagString;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

@SuppressWarnings("unused")
/**
 * Utility class for common nbt functions
 * @author Sam54123
 * @deprecated use SNBTUtil instead.
 */
public class NBTStrings {
	/**
	 * Convert nbt to a string.
	 * @param nbt NBT input.
	 * @return Generated string.
	 * @deprecated Use SNBTUtil.toSNBT instead.
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
	 * @deprecated use SNBTUtil.toSNBT instead.
	 */
	public static String tagToString(Tag<?> tag) {
		try {
			return SNBTUtil.toSNBT(tag);
		} catch (IOException e) {
			e.printStackTrace();
			return "{}";
		}
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
//			NBTTag tag = listIterator.next();
//			
//			listString = listString+tagToString(tag);
//			if (listIterator.hasNext())  {
//				listString = listString+',';
//			}
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
	 * @throws IllegalArgumentException If the string is formatted improperly.
	 * @deprecated Use SNBTUtil.fromSNBT instead.
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
	 * @deprecated use SNBTUtil.fromSNBT instead.
	 */
	public static Tag<?> parseTag(String inString) throws IllegalArgumentException {
		try {
			return SNBTUtil.fromSNBT(inString);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
	
	private static NBTTagList parseList(String inString) throws IllegalArgumentException {
		// Remove newlines and whitespace
		inString = inString.trim();
		inString = inString.replace("\n", "");
		
		// Check for brackets
		if (!(inString.charAt(0) == '[' && inString.charAt(inString.length()-1) == ']')) {
			throw new IllegalArgumentException("NBT List String is missing brackets! ([])");
		}
		
		// Remove brackets
		inString = inString.substring(1, inString.length() - 1);

		// Split string into tags
		String[] stringTags = splitString(inString, ',');
		
		List<NBTTag> tags = new ArrayList<NBTTag>();
		for (String s : stringTags) {
//			tags.add(parseTag(s));
		}
		
		return new NBTTagList(tags);
	}
	
	private static String parseString(String in) {
		in = in.replace("\\\"", "\""); // Replace '\"' with '"'
		return in.substring(1,in.length()-1); // Remove quotes
	}
	
	public static byte parseByte(String in) throws IllegalArgumentException {
		if (in.charAt(in.length()-1) != 'b') {
			throw new IllegalArgumentException("Unable to parse byte: "+in);
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
