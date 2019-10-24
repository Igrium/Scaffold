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
	 * @return Scoreboard objective name.
	 */
	public String getObjectiveName() {
		return "timeline."+getLevel().getName()+"."+getName()+".frame";
	}

	@Override
	public boolean compileLogic(Datapack datapack) {
		if  (!super.compileLogic(datapack)) {
			return false;
		}
		
		MCFunction timelineTick = new MCFunction("timeline."+getName()+".tick");
		
		getLevel().initFunction().addCommand("scoreboard objectives add "+getObjectiveName()+" dummy");
		
		return true;
	}
}
