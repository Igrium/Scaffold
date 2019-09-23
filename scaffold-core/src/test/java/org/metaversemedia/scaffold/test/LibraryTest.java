package org.metaversemedia.scaffold.test;

import java.io.IOException;

import org.junit.Test;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.level.entity.GameEntity;
import org.metaversemedia.scaffold.logic.Datapack;
import org.metaversemedia.scaffold.logic.MCFunction;
import org.metaversemedia.scaffold.math.Vector;

public class LibraryTest {

	@Test
	public void test() {
		Project project = Project.init("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject", "Test Project");
		
		Level level = new Level(project);
		
		GameEntity ent1 = (GameEntity) level.newEntity(GameEntity.class, "ent1", new Vector(0,0,0));

		Datapack datapack = new Datapack(project);
		datapack.setDescription("This is a test datapack!");
		
		MCFunction function = new MCFunction("test function");
		function.commands().add("say This function's name is $functionname");
		function.commands().add("say This function's namespace is $namespace");
		
		datapack.functions.add(function);
		
		try {
			datapack.compile(project.assetManager().getAbsolutePath("logicTest/datapacks/packTest"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
	