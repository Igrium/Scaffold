package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.MCFunction;

public class Timeline extends Entity {
	
	/**
	 * Represents the timeline outputs into the IO system.
	 */
	public class OutputList {
		public final Map<String, String> outputs = new HashMap<String, String>();
		
		public OutputList() {
			
		}
		
		/**
		 * Construct an OutputList from a JSONObject.
		 * @param in JSONObject to parse.
		 */
		public OutputList(JSONObject in) {
			for (String k : in.keySet()) {
				outputs.put(k, in.getString(k));
			}
		}
	}
	
	public Timeline(Level level, String name) {
		super(level, name);
		setAttribute("outputs", new OutputList());
	}
	
	/**
	 * Get the scoreboard objective the timeline is using to store it's frame.
	 * @return Frame objective name.
	 */
	public String getFrameObjective() {
		return "timeline."+getLevel().getName()+"."+getName()+".frame";
	}
	
	/**
	 * Get the scoreboard objective used to represent whether this timeline is playing.
	 * @return Playing objective name.
	 */
	public String getPlayingObjective() {
		return "timeline."+getLevel().getName()+"."+getName()+".playing";
	}
	
	@Override
	public List<AttributeDeclaration> getAttributeFields() {
		List<AttributeDeclaration> attributes = super.getAttributeFields();
		attributes.add(new AttributeDeclaration("outputs", OutputList.class));
		return attributes;
	}

	@Override
	public boolean compileLogic(Datapack datapack) {
		if  (!super.compileLogic(datapack)) {
			return false;
		}
		
		getLevel().initFunction().addCommand("scoreboard objectives add "+getFrameObjective()+" dummy");
		getLevel().initFunction().addCommand("scoreboard objectives add "+getPlayingObjective()+" dummy");
		
		MCFunction timelineTick = new MCFunction("timeline."+getName()+".tick");
		writeTickFunction(timelineTick);
		
		getLevel().getDatapack().functions.add(timelineTick);
		// Make level run function every tick if playing.
		getLevel().tickFunction()
				.addCommand("execute as " + getLevel().getScoreboardEntity().getTargetSelector()
						+ " if score @s "+ getPlayingObjective() + " matches 1 run "
						+ getLevel().getDatapack().formatFunctionCall(timelineTick));
		
		return true;
	}
	
	/**
	 * Write the function that the timeline calls each frame.
	 * Runs in the context of the map scoreboard entity.
	 * @param tickFunction tick function to write to.
	 */
	protected void writeTickFunction(MCFunction tickFunction) {
		// Compile output list.
		Map<String,String> outputs = ((OutputList) getAttribute("outputs")).outputs;
		for (String frame : outputs.keySet()) {
			
			if (outputConnections().contains(outputs.get(frame))) { // Make sure output is set.
				// Compile single output.
				String[] commands = compileOutput(outputs.get(frame), this);
				
				for (String c : commands) {
					tickFunction.addCommand("execute if score @s "+getFrameObjective()+" matches "+frame+" run "+c);
				}
				
			}
			
		}
		
		tickFunction.addCommand("scoreboard players add @s "+getFrameObjective()+" 1");
	}
	
	@Override
	public JSONObject serialize() {
		JSONObject serialized = super.serialize();
		OutputList outputs = (OutputList) getAttribute("outputs");
		
		// Make sure outputs are serialized properly.
		serialized.getJSONObject("attributes").put("outputs", new JSONObject(outputs.outputs));
		
		return serialized;
	}
	
	@Override
	protected void onUnserialized(JSONObject object) {
		super.onUnserialized(object);
		setAttribute("outputs", new OutputList(object.getJSONObject("attributes").getJSONObject("outputs")));
	}
}
