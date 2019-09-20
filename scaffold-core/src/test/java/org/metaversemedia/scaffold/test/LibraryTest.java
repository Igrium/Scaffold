package org.metaversemedia.scaffold.test;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;
import org.metaversemedia.scaffold.core.Project;

public class LibraryTest {

	@Test
	public void test() {
		System.out.println("Testing!");
		Project project = Project.loadProject("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject");
		System.out.println(project.getProjectFolder());
		System.out.println(project.gameInfo().getTitle());
		ArrayList<String> loadedPaths = project.gameInfo().getLoadedPaths();
		
		for (int i = 1; i < loadedPaths.size(); i++) {
			System.out.println(loadedPaths.get(i));
		}
	}

}
	