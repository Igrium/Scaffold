package org.metaversemedia.scaffold.level.entity.logic;

import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.logic.Datapack;

/**
 * Fires an output on level load.
 * 
 * @author Sam54123
 */
public class Auto extends Entity {

	public Auto(Level level, String name) {
		super(level, name);
	}

	@Override
	public boolean compileLogic(Datapack datapack) {
		if (!super.compileLogic(datapack)) {
			return false;
		}
		
		String[] loadCommands = compileOutput("OnLoad", this);
		
		for (String s : loadCommands) {
			getLevel().initFunction().addCommand(s);
		}
		
		String[] tickCommands = compileOutput("OnTick", this);
		
		for (String s : tickCommands) {
			getLevel().tickFunction().addCommand(s);
		}
		
		return true;
	}
	
	@Override
	public String getRenderAsset() {
		return "scaffold/textures/editor/auto.png";
	}

}
