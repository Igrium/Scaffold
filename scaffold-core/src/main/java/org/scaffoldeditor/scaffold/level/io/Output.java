package org.scaffoldeditor.scaffold.level.io;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scaffoldeditor.scaffold.level.entity.Entity;

/**
 * Represents an entity output connection
 * 
 * @author Sam54123
 */
public class Output {
	private Entity parent;

	/**
	 * Create a new output connection.
	 * 
	 * @param parent Parent entity.
	 */
	public Output(Entity parent) {
		this.parent = parent;
	}

	/**
	 * The name of the output to trigger on.
	 */
	public String name = "";
	
	/**
	 * Name of the entity to target.
	 */
	public String target = "";
	
	/**
	 * Name of the input to trigger.
	 */
	public String input = "";

	/**
	 * Arguements to call the input with.
	 */
	public List<String> args = new ArrayList<String>();
	
	/**
	 * Check if the output is valid to call.
	 * @return Validity.
	 */
	public boolean isValid() {
		return (!name.matches("") && !target.matches("") && !input.matches(""));
	}
	
	/**
	 * Compile this output into a Minecraft command.
	 * @param instigator Instigator entity.
	 * @return Compiled command.
	 */
	public String compile(Entity instigator) {
		Entity target = parent.getLevel().getEntity(this.target);
		if (target == null) {
			return "";
		}
		Input connection = target.getInput(input);

		
		if (connection != null) {
			return connection.getCommand(instigator, parent, args.toArray(new String[0]));
		} else {
			return "";
		}
	}
	
	/**
	 * Serialize this output into a JSONObject.
	 * @return Serialized output.
	 */
	public JSONObject serialize() {
		JSONObject object = new JSONObject();
		
		object.put("name", name);
		object.put("target", target);
		object.put("input", input);
		object.put("args", args);
		
		return object;
	}
	
	/**
	 * Unserialize an output from a JSONObject.
	 * @param object JSONObject to unserialize.
	 * @param parent Output's parent entity.
	 * @return Unserialized object.
	 */
	public static Output unserialize(JSONObject object, Entity parent) {
		try {
			Output output = new Output(parent);
			
			output.name = object.getString("name");
			output.target = object.getString("target");
			output.input = object.getString("input");
			
			JSONArray argArray = object.getJSONArray("args");
			for (Object s : argArray) {
				output.args.add((String) s);
			}
			
			return output;
			
		} catch (JSONException e) {
			System.out.println("Unable to parse output: "+object);
			return null;
		}
	}

}
