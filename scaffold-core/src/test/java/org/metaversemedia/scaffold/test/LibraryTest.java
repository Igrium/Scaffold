package org.metaversemedia.scaffold.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.level.entity.Faceable;
import org.metaversemedia.scaffold.level.entity.GameEntity;
import org.metaversemedia.scaffold.math.Vector;
import org.metaversemedia.scaffold.nbt.schematic.Schematic;
import org.metaversemedia.scaffold.nbt.schematic.Structure;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.ShortTag;
import com.flowpowered.nbt.stream.NBTInputStream;

public class LibraryTest {

	@Test
	public void test() {
//		Project project = Project.init("/Users/h205p1/Documents/ProgramingProjects/Scaffold/testProject", "Test Project");
		Project project = Project.init("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject", "Test Project");

		
		Level level = new Level(project);
		
		GameEntity ent1 = (GameEntity) level.newEntity(GameEntity.class, "ent1", new Vector(0,0,0));
		Faceable faceable = (Faceable) level.newEntity(Faceable.class, "faceable", new Vector(0,0,0));
		
		for (Entity e : level.getEntities().values()) {
			System.out.println(e.getClass());
		}

		level.saveFile("maps/testLevel.mclevel");
		Level level2 = Level.loadFile(project, "maps/testLevel.mclevel");
		
		for (Entity e : level2.getEntities().values()) {
			System.out.println(e.getClass());
		}
		
//		level.compile(project.assetManager().getAbsolutePath("game/saves/world"));
		
//		try {
//			Structure structure = Structure.fromFile(project.assetManager().getAbsolutePath("schematics/house.nbt").toFile());
//			System.out.println(structure);
//			System.out.println(structure.blockAt(5, 3, 4));
////			System.out.println(structure.blockAt(1, 1, 1));
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
	