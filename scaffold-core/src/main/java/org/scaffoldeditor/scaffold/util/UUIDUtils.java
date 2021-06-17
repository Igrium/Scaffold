package org.scaffoldeditor.scaffold.util;

import java.util.UUID;

/**
 * A variety of utility methods for manipulating UUIDs.
 * @author Igrium
 */
public final class UUIDUtils {

	/**
	 * Get a uuid represented as four 32 bit numbers. Each part is stored in an
	 * integer array ordered from most significant to least significant. An example
	 * of this representation would be
	 * <code>[I;-132296786,2112623056,-1486552928,-920753162]</code>
	 * 
	 * @param uuid
	 * @return
	 */
	public static int[] toIntArray(UUID uuid) { 
		return toIntArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}
	
	private static int[] toIntArray(long most, long least) {
		return new int[] { (int) (most >> 32), (int) most, (int) (least >> 32), (int) least };
	}
	
	/**
	 * Parse a UUID from an int array as defined in {@link #toIntArray(UUID)}.
	 * @param array Array to parse.
	 * @return The UUID.
	 */
	public static UUID fromIntArray(int[] array) {
		return new UUID((long)array[0] << 32 | (long)array[1] & 4294967295L, (long)array[2] << 32 | (long)array[3] & 4294967295L);
	}
}
