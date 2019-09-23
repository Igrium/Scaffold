package org.metaversemedia.scaffold.test;

import org.junit.Test;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.GameEntity;
import org.metaversemedia.scaffold.math.Vector;

public class LibraryTest {

	@Test
	public void test() {
		Project project = Project.init("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject", "Test Project");
		
		Level level = new Level(project);
		
		GameEntity ent1 = (GameEntity) level.newEntity(GameEntity.class, "ent1", new Vector(0,0,0));

		level.saveFile("maps/testLevel.mclevel");
		level.compile(project.assetManager().getAbsolutePath("game/saves/world"));
	}

}
	