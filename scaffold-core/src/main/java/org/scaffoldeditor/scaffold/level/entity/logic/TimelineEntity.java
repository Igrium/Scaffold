package org.scaffoldeditor.scaffold.level.entity.logic;

import java.io.IOException;
import org.json.JSONException;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.io.Input;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.MCFunction;
import org.scaffoldeditor.scaffold.logic.timeline.Timeline;
import org.scaffoldeditor.scaffold.logic.timeline.TimelineEvent;
import org.scaffoldeditor.scaffold.util.JSONUtils;

public class TimelineEntity extends Entity {
	
	// Represents the current timeline, regardless of if it is loaded from disk.
	protected Timeline timeline = new Timeline();
	
	// Keep track of timeline file seperately from attribute for optimization.
	private String oldFile = "";

	public TimelineEntity(Level level, String name) {
		super(level, name);
		setAttribute("file", new StringAttribute(name), true);
		
		this.registerInput(new Input(this) {

			@Override
			public String getName() {
				return "Start";
			}

			@Override
			public boolean takesArgs() {
				return false;
			}

			@Override
			public String getCommand(Entity instigator, Entity caller, String[] args) {
				// If frame is less than timeline length, set playing objective to 1.
				return "execute as " + getLevel().getScoreboardEntity().getTargetSelector() + " if score @s "
						+ getFrameObjective() + " matches 0.." + (timeline.length() - 1)
						+ " run scoreboard players set @s " + getPlayingObjective() + " 1";
			}
			
		});
		
		this.registerInput(new Input(this) {

			@Override
			public String getName() {
				return "Stop";
			}

			@Override
			public boolean takesArgs() {
				return false;
			}

			@Override
			public String getCommand(Entity instigator, Entity caller, String[] args) {
				return "scoreboard players set " + getLevel().getScoreboardEntity().getTargetSelector() + " "
						+ getPlayingObjective() + " 0";
			}
			
		});
		
		this.registerInput(new Input(this) {

			@Override
			public String getName() {
				return "SetFrame";
			}

			@Override
			public boolean takesArgs() {
				return true;
			}

			@Override
			public String getCommand(Entity instigator, Entity caller, String[] args) {
				// Make sure arguements were passed correctly.
				try {
					int frame = Integer.parseInt(args[0]);
					
					// Make sure frame is in range.
					if (frame >= timeline.length()) {
						return "say Frame "+frame+" is out of range 0-"+(timeline.length()-1);
					}
				} catch (NullPointerException | NumberFormatException e) {
					return "say Unable to set frame!";
				}
				
				return "scoreboard players set "+getLevel().getScoreboardEntity().getTargetSelector()+" "
						+getFrameObjective()+" "+args[0];
			}
			
		});
	}
	
	@Override
	public void onUpdateAttributes() {
		super.onUpdateAttributes();
		
		if (!((StringAttribute) getAttribute("file")).getValue().matches(oldFile)) {
			
			// Load timeline from file.
			try {
				timeline = Timeline.unserialize(JSONUtils
						.loadJSON(getLevel().getProject().assetManager().findAsset(((StringAttribute) getAttribute("file")).getValue())));
				System.out.println("Loaded timeline: "+getAttribute("file"));
			} catch (JSONException | IOException e) {
				System.out.println("Unable to load timeline: "+getAttribute("file"));
			}
		}
	}

	
	/**
	 * Get the scoreboard objective the timeline is using to store it's frame.
	 * @return Frame objective name.
	 */
	public String getFrameObjective() {
		String s = ("s."+getName()+".frame").toLowerCase();
		
		// Scoreboard objective must be less than 16 chars.
		if (s.length() > 16) {
			return s.substring(s.length()-16);
		} else {
			return s;
		}
	}
	
	/**
	 * Get the scoreboard objective used to represent whether this timeline is playing.
	 * @return Playing objective name.
	 */
	public String getPlayingObjective() {
		String s = ("s."+getName()+".playing").toLowerCase();
		if (s.length() > 16) {
			return s.substring(s.length()-16);
		} else {
			return s;
		}
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		if  (!super.compileLogic(datapack)) {
			return false;
		}
		
		getLevel().initFunction().addCommand("scoreboard objectives add "+getFrameObjective()+" dummy");
		getLevel().initFunction().addCommand("scoreboard objectives add "+getPlayingObjective()+" dummy");
		
		// Initialize objective so that set Start can check for it.
		getLevel().initFunction().addCommand("scoreboard players set "
				+ getLevel().getScoreboardEntity().getTargetSelector() + " " + getFrameObjective() + " 0");
		
		MCFunction timelineTick = new MCFunction(getName()+".tick");
		writeTickFunction(timelineTick);
		getLevel().getDatapack().functions.add(timelineTick);
		
		// Make level run function every tick if playing.
		getLevel().tickFunction()
				.addCommand("execute as " + getLevel().getScoreboardEntity().getTargetSelector()
						+ " if score @s "+ getPlayingObjective() + " matches 1 run function "
						+ getLevel().getDatapack().formatFunctionCall(timelineTick));
		
		return true;
	}
	
	/**
	 * Write the function that the timeline calls each frame.
	 * Runs in the context of the map scoreboard entity.
	 * @param tickFunction tick function to write to.
	 */
	public void writeTickFunction(MCFunction tickFunction) {
		
		// Compile output list.
		for (TimelineEvent e : timeline) {
			if (hasOutput(e.name)) { // Only run if output connection is set.
				String[] commands = compileOutput(e.name, this);
				for (String c : commands) {
					tickFunction.addCommand("execute if score @s "+getFrameObjective()+" matches "+e.frame+" run "+c);
				}
			}
		}
		
		tickFunction.addCommand("scoreboard players add @s "+getFrameObjective()+" 1");
		
		// Stop timeline once length is reached.
		tickFunction.addCommand("execute if score @s "+getFrameObjective()+" matches "+timeline.length()+
				" run scoreboard players set @s "+getPlayingObjective()+" 0");
		
	}

	@Override
	public String getDefaultName() {
		return "timeline";
	}
	
	@Override
	public String getRenderAsset() {
		return "scaffold/textures/editor/clock.png";
	}
}
