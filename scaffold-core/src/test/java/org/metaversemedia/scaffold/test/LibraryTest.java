package org.metaversemedia.scaffold.test;

import org.junit.Test;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.level.entity.Rotatable;
import org.metaversemedia.scaffold.math.Vector;

public class LibraryTest {

	@Test
	public void test() {
		Project project = Project.init("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject", "Test Project");
		
		Level level = new Level(project);
		
		Entity ent1 = level.newEntity(Rotatable.class, "ent1", new Vector(0,0,0));
		
		level.compileLogic("logicTest/level1");
		
		level.saveFile("maps/level1.mcmap");
	}

}
	