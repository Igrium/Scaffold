package org.metaversemedia.scaffold.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.GameEntity;
import org.metaversemedia.scaffold.math.Vector;
import org.metaversemedia.scaffold.nbt.Schematic;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.ShortTag;
import com.flowpowered.nbt.stream.NBTInputStream;

public class LibraryTest {

	@Test
	public void test() {
//		Project project = Project.init("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject", "Test Project");
//		
//		Level level = new Level(project);
//		
//		GameEntity ent1 = (GameEntity) level.newEntity(GameEntity.class, "ent1", new Vector(0,0,0));
//
//		level.saveFile("maps/testLevel.mclevel");
//		level.compile(project.assetManager().getAbsolutePath("game/saves/world"));
		
		NBTInputStream input = null;
		try {
			input = new NBTInputStream(new FileInputStream("/Users/h205p1/Documents/ProgramingProjects/Scaffold/testProject/jewel-of-the-sea.schematic"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		try {
			CompoundTag tag = (CompoundTag) input.readTag();
//			System.out.println(tag);
			CompoundMap map1 = (CompoundMap) tag.getValue();
			System.out.println(map1.keySet());
			
			ShortTag widthTag = (ShortTag) map1.get("Width");
			System.out.println(widthTag.getValue());
			
			Schematic schematic = Schematic.fromCompoundMap(map1);
					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
	