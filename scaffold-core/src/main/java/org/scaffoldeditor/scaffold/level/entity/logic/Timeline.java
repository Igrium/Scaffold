package org.scaffoldeditor.scaffold.level.entity.logic;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.MCFunction;

public class Timeline extends Entity {
	
	public Timeline(Level level, String name) {
		super(level, name);
		// TODO Auto-generated constructor stub
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
		tickFunction.addCommand("scoreboard players add @s "+getFrameObjective()+" 1");
	}
}
