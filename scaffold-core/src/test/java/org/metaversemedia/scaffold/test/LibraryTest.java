package org.metaversemedia.scaffold.test;

import java.io.IOException;

import org.junit.Test;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.LevelData;
import org.metaversemedia.scaffold.level.entity.BlockCollectionEntity;
import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.level.entity.GameEntity;
import org.metaversemedia.scaffold.level.entity.StructureEntity;
import org.metaversemedia.scaffold.math.Vector;

public class LibraryTest {

	@Test
	public void test() {
//		Project project = Project.init("/Users/h205p1/Documents/ProgramingProjects/Scaffold/testProject", "Test Project");
		Project project = Project.init("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject", "Test Project");

		
		Level level = new Level(project, "Test Level");
//		Level level = Level.loadFile(project, "maps/testLevel.mclevel");
		
		GameEntity ent1 = (GameEntity) level.newEntity(GameEntity.class, "ent1", new Vector(0,0,0));
		BlockCollectionEntity structureEntity = (BlockCollectionEntity) level.newEntity(StructureEntity.class, "house", new Vector(0,0,0));
		structureEntity.setAttribute("file", "schematics/house.nbt");
		
		System.out.println(structureEntity.getBlockCollection());
		
		for (Entity e : level.getEntities().values()) {
			System.out.println(e.getClass());
		}
		
		level.saveFile("maps/testLevel.mclevel");
		level.compile(project.assetManager().getAbsolutePath("game/saves/testLevel"));
		
		
//		try {
//			Structure structure = Structure.fromFile(project.assetManager().getAbsolutePath("schematics/house.nbt").toFile());
//			System.out.println(structure);
//			System.out.println(structure.blockAt(5, 3, 4));
//			System.out.println(structure.blockAt(1, 1, 1));
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
}
	