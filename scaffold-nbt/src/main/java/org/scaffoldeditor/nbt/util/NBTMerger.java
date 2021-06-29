package org.scaffoldeditor.nbt.util;

import org.apache.logging.log4j.LogManager;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.Tag;

/**
 * Utility class for merging NBT tags.
 * @author Igrium
 */
public final class NBTMerger {
	private NBTMerger() {};
	
	public static enum ListMergeMode {
		APPEND, PREPEND, REPLACE
	};
	
	/**
	 * Merge two compound tags.
	 * @param tag1 Tag to merge into (will be modified).
	 * @param tag2 Tag to merge (will not be modified).
	 * @param override Whether to override existing tags.
	 * @param mergeMode How to handle list tags.
	 */
	@SuppressWarnings("unchecked")
	public static void mergeCompound(CompoundTag tag1, CompoundTag tag2, boolean override, ListMergeMode mergeMode) {
		for (String name : tag2.keySet()) {
			Tag<?> subject = tag2.get(name);
			
			if (subject instanceof ListTag<?>) {
				if (tag1.containsKey(name) && tag1.get(name) instanceof ListTag) {
					if (mergeMode == ListMergeMode.REPLACE) {
						if (override) {
							tag1.put(name, subject);
						} else {
							continue;
						}
					} else {
						
						@SuppressWarnings("rawtypes")
						ListTag target = tag1.getListTag(name);
						ListTag<?> subjectList = (ListTag<?>) subject;
						if (!target.getTypeClass().isAssignableFrom(subjectList.getTypeClass())) {
							LogManager.getLogger()
									.error("Unable to merge list tag of type "
											+ subjectList.getTypeClass().getSimpleName() + " with list tag of type "
											+ target.getTypeClass().getSimpleName());
						}
						mergeList(target, subjectList, mergeMode);
					}
				} else {
					tag1.put(name, subject);
				}
				
			} else if (subject instanceof CompoundTag) {
				if (tag1.containsKey(name) && tag1.get(name) instanceof CompoundTag) {
					mergeCompound(tag1.getCompoundTag(name), (CompoundTag) subject, override, mergeMode);
				} else if (override) {
					tag1.put(name, subject);
				}
			} else if (override || !tag1.containsKey(name)) {
				tag1.put(name, subject);
			}
		}
	}
	
	public static <T extends Tag<?>> void mergeList(ListTag<T> list1, ListTag<T> list2, ListMergeMode mergeMode) {
		switch(mergeMode) {
		case APPEND:
			for (T tag : list2) {
				list1.add(tag);
			}
			break;
		case PREPEND:
			for (T tag : list2) {
				list1.add(0, tag);
			}
			break;
		default:
			throw new IllegalArgumentException("Can't merge lists with merge mode REPLACE.");
		}
	}
}
