package org.metaversemedia.scaffold.test;

import org.json.JSONObject;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.logic.Datapack;

public class TestEntity extends Entity { 

	public TestEntity(Level level, String name) {
		super(level, name);
		setAttribute("number", 23);
		System.out.println("test entity: "+name);
	}
	
	@Override
	public JSONObject serialize() {
		JSONObject object = super.serialize();
		object.put("_comment_", "This is a test entity!");
		return object;
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		super.compileLogic(datapack);
		
		getLevel().initFunction().commands().add("/say This test entity is called "+getName()+"!");
		
		return true;
	}

}
