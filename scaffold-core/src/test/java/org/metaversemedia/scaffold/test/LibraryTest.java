package org.metaversemedia.scaffold.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONObject;
import org.junit.Test;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.logic.MCFunction;
import org.metaversemedia.scaffold.math.Vector;

public class LibraryTest {

	@Test
	public void test() {
		Project project = Project.init("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject", "Test Project");
		
		MCFunction function = new MCFunction();
		
		function.variables().put("testVariable", "Test_Variable");
		
		function.commands().add("say this is a function!");
		function.commands().add("say Variable: $testVariable");
		
		try {
			function.compile(project.assetManager().getAbsolutePath("function.mcfunction").toFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assert(false);
		}
		
	}

}
	