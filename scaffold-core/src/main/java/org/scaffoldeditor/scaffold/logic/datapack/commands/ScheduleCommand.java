package org.scaffoldeditor.scaffold.logic.datapack.commands;

import org.scaffoldeditor.nbt.util.Identifier;

/**
 * Represents a
 * <a href="https://minecraft.fandom.com/wiki/Commands/schedule">schedule
 * command</a>.
 * 
 * @author Igrium
 */
public class ScheduleCommand implements Command {
	public enum Mode {
		/**
		 * Simply replaces the current function's schedule time
		 */
		REPLACE,
		/**
		 * Allows multiple schedules to exist at different times.
		 */
		APPEND
	}
	
	/**
	 * The function to call when the timer is complete.
	 */
	public final Identifier function;
	/**
	 * The amount of ticks to wait before calling the function.
	 */
	public final int delay;
	/**
	 * Whether to add or replace the function's schedule time.
	 */
	public final Mode mode;
	
	/**
	 * Create a schedule command using {@link Mode#REPLACE}.
	 * @param function The function to call when the timer is complete.
	 * @param delay The amount of ticks to wait before calling the function.
	 */
	public ScheduleCommand(Identifier function, int delay) {
		this.function = function;
		this.delay = delay;
		this.mode = Mode.REPLACE;
	}
	
	/**
	 * Create a schedule command.
	 * @param function The function to call when the timer is complete.
	 * @param delay The amount of ticks to wait before calling the function.
	 * @param mode Whether to add or replace the function's schedule time.
	 */
	public ScheduleCommand(Identifier function, int delay, Mode mode) {
		this.function = function;
		this.delay = delay;
		this.mode = mode;
	}

	@Override
	public String compile() {
		String mode = this.mode == Mode.APPEND ? "append" : "replace";
		return "schedule function "+function.toString()+" "+delay+"t "+mode;
	}
}
