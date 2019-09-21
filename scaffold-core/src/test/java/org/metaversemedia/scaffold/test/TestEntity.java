package org.metaversemedia.scaffold.test;

import org.json.JSONObject;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.Entity;

public class TestEntity extends Entity { 

	public TestEntity(Level level, String name) {
		super(level, name);
		System.out.println("test entity: "+name);
	}
	
	@Override
	public JSONObject serialize() {
		JSONObject object = super.serialize();
		object.put("_comment_", "This is a test entity!");
		return object;
	}

}
