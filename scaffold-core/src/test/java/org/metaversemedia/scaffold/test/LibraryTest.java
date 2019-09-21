package org.metaversemedia.scaffold.test;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONObject;
import org.junit.Test;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.math.Vector;

public class LibraryTest {

	@Test
	public void test() {
		Project project = Project.init("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject", "Test Project");
		
		Level level = new Level(project);
		
		Entity entity = level.newEntity(Entity.class, "Entity", new Vector(0,0,0));
		Entity entity1 = level.newEntity(Entity.class, "Entity", new Vector(0,0,0));
		Entity entity2 = level.newEntity(Entity.class, "Entity", new Vector(0,0,0));
		
		level.saveFile("maps/leveltest.mcmap");
	}

}
	