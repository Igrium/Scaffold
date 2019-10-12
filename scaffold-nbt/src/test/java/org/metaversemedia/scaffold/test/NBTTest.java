package org.metaversemedia.scaffold.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.metaversemedia.scaffold.nbt.NBTStrings;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.FloatTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;

public class NBTTest {

	@Test
	public void test() {
		System.out.println("test");
		CompoundMap testMap = new CompoundMap();
		testMap.put(new StringTag("TestTag","test"));
		testMap.put(new FloatTag("FloatTag",5.0f));
		
		List<StringTag> list = new ArrayList<StringTag>();
		list.add(new StringTag("", "Test \" Test"));
		list.add(new StringTag("", "Test2!"));
		
		ListTag<StringTag> listTag = new ListTag<StringTag>("testList", StringTag.class, list);
		testMap.put(listTag);
		
		System.out.println(NBTStrings.tagToString(listTag));
				
		String nbtString =
				"{CustomName:\"\\\"Test\\\"\",CustomNameVisible:1,NoAI:1b,ExplosionRadius:4,HandDropChances:[2F,2F],HandItems:[{},{id:\"minecraft:stone\",Count:1}],Tags:[\"test\"]}";
		
		System.out.println(nbtString);;
		
		try {
			CompoundMap map = NBTStrings.nbtFromString(nbtString);
			CompoundTag tag =  new CompoundTag("test", map);
			System.out.println(NBTStrings.tagToString(tag));
		} catch (IOException | NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
//		System.out.println(Short.parseShort("3s"));
		assert(true);
	}

}
