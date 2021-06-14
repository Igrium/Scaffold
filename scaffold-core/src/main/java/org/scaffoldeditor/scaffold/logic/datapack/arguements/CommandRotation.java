package org.scaffoldeditor.scaffold.logic.datapack.arguements;

/**
 * Represents a rotation as represented in a Minecraft command.
 * @author Igrium
 */
public class CommandRotation {
	public enum Mode { GLOBAL, RELATIVE }
	
	public final double yaw;
	public final double pitch;
	public final Mode mode;
	
	public CommandRotation(double yaw, double pitch, Mode mode) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.mode = mode;
	}
	
	public CommandRotation(double yaw, double pitch) {
		this(yaw, pitch, Mode.GLOBAL);
	}
	
	@Override
	public String toString() {
		return "[" + yaw + ", " + pitch + "]";
	}
	
	/**
	 * Get this rotation as a string that can be inserted into a command.
	 * @return Command string.
	 */
	public String getString() {
		String prefix = "";
		if (mode == Mode.RELATIVE) prefix = "~";
		return prefix+yaw+" "+prefix+pitch;
	}
}
