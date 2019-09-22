package org.metaversemedia.scaffold.test;

import java.nio.file.Path;

import org.json.JSONObject;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.Entity;

public class TestEntity extends Entity { 

	public TestEntity(Level level, String name) {
		super(level, name);
		addAttribute("number", 23);
		System.out.println("test entity: "+name);
	}
	
	@Override
	public JSONObject serialize() {
		JSONObject object = super.serialize();
		object.put("_comment_", "This is a test entity!");
		return object;
	}
	
	@Override
	public boolean compileLogic(Path logicFolder) {
		super.compileLogic(logicFolder);
		
		getLevel().initFunction().commands().add("/say This test entity is called "+getName()+"!");
		
		return true;
	}

}
