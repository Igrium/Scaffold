package org.metaversemedia.scaffold.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.GameEntity;
import org.metaversemedia.scaffold.math.Vector;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
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
			input = new NBTInputStream(new FileInputStream("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject\\the-small-yacht.schematic"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		try {
			CompoundMap map = (CompoundMap) input.readTag().getValue();
			ListTag<CompoundTag> tileEntities = (ListTag<CompoundTag>) map.get("TileEntities");
			System.out.println(map.get("Width").getValue());
			
			//System.out.println(tileEntities.getValue());
			
			List<CompoundTag> entities = tileEntities.getValue();
			
			System.out.println(entities.get(1).getValue());
					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
	