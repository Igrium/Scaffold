package org.metaversemedia.scaffold.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.scaffoldeditor.nbt.NBTStrings;

import mryurihi.tbnbt.tag.NBTTag;
import mryurihi.tbnbt.tag.NBTTagCompound;
import mryurihi.tbnbt.tag.NBTTagFloat;
import mryurihi.tbnbt.tag.NBTTagList;
import mryurihi.tbnbt.tag.NBTTagString;


public class NBTTest {

	@Test
	public void test() {
		System.out.println("test");
		NBTTagCompound testMap = new NBTTagCompound(new HashMap<String, NBTTag>());
		testMap.put("TestTag", new NBTTagString("test"));
		testMap.put("FloatTag", new NBTTagFloat(5.0f));
		
		List<NBTTag> list = new ArrayList<NBTTag>();
		list.add(new NBTTagString("Test \" Test"));
		list.add(new NBTTagString("Test2!"));
		
		NBTTagList listTag = new NBTTagList(list);
		testMap.put("ListTest",listTag);
		
		System.out.println(NBTStrings.tagToString(testMap));
				
		String nbtString =
				"{CustomName:\"\\\"Test\\\"\",CustomNameVisible:1,NoAI:1b,ExplosionRadius:4,HandDropChances:[2F,2F],HandItems:[{},{id:\"minecraft:stone\",Count:1}],Tags:[\"test\"]}";
		
		System.out.println(nbtString);;
		
		try {
			NBTTagCompound map = NBTStrings.nbtFromString(nbtString);
			System.out.println(NBTStrings.tagToString(map));
		} catch (IOException | NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
//		System.out.println(Short.parseShort("3s"));
		assert(true);
	}

}
