package org.scaffoldeditor.scaffold.logic.datapack.commands;

import org.scaffoldeditor.scaffold.logic.datapack.arguements.BlockArguement;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3i;

public class SetBlockCommand implements Command {
	public enum Mode { DESTROY, KEEP, REPLACE }
	
	public final CommandVector3i pos;
	public final BlockArguement block;
	public final Mode mode;
	
	public SetBlockCommand(CommandVector3i pos, BlockArguement block, Mode mode) {
		this.pos = pos;
		this.block = block;
		this.mode = mode;
	}

	@Override
	public String compile() {
		String modeString;
		if (mode == Mode.DESTROY) modeString = "destroy";
		else if (mode == Mode.KEEP ) modeString = "keep";
		else modeString = "replace";
		
		return "setblock "+pos.getString()+" "+block.compile()+" "+modeString;
	}

}
