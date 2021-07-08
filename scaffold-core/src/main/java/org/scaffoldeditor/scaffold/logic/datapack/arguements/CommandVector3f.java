package org.scaffoldeditor.scaffold.logic.datapack.arguements;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector.Mode;

/**
 * Represents a vector as represented in a Minecraft command.
 * @author Igrium
 */
public class CommandVector3f extends Vector3f {
		
	public static class ModeStrings {
		public static final String GLOBAL = "";
		public static final String RELATIVE = "~";
		public static final String LOCAL = "^";
	}
	
	public final Mode mode;

	public CommandVector3f(float x, float y, float z) {
		super(x, y, z);
		this.mode = Mode.GLOBAL;
	}
	
	public CommandVector3f(float x, float y, float z, Mode mode) {
		super(x, y, z);
		this.mode = mode;
	}
	
	public CommandVector3f(Vector3f vec, Mode mode) {
		super(vec.x, vec.y, vec.z);
		this.mode = mode;
	}
	
	public CommandVector3f(Vector3f vec) {
		this(vec, Mode.GLOBAL);
	}
	
	public static CommandVector3f fromString(String in) {
		char modeChar = in.charAt(0);
		Mode mode;
		if (modeChar == '~') mode = Mode.RELATIVE;
		else if (modeChar == '^') mode = Mode.LOCAL;
		else mode = Mode.GLOBAL;
		
		in = in.replace("~", "").replace("^", "");
		String[] split = in.split(" ");
		
		return new CommandVector3f(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]), mode);
	}
	
	/**
	 * Get this vector as a string that can be inserted into a command.
	 * @return Command string.
	 */
	public String getString() {
		String prefix;
		switch(mode) {
			case GLOBAL:
				prefix = ModeStrings.GLOBAL;
				break;
			case RELATIVE:
				prefix = ModeStrings.RELATIVE;
				break;
			case LOCAL:
				prefix = ModeStrings.LOCAL;
				break;
			default:
				prefix = "";
				break;
		}
		
		return prefix+x+" "+prefix+y+" "+prefix+z;
	}
	
	@Override
	public String toString() {
		return getString();
	}

}
