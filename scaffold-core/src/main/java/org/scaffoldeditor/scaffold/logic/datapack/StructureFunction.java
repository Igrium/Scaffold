package org.scaffoldeditor.scaffold.logic.datapack;

import java.util.ArrayList;
import java.util.List;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.BlockArguement;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector.Mode;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.SetBlockCommand;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3i;

/**
 * Represents a function containing a set of <code>setblock</code> commands that
 * will spawn a block collection when triggered, relative to its execute location.
 * 
 * @author Igrium
 */
public class StructureFunction extends AbstractFunction {
	
	/**
	 * The structure this function will place.
	 */
	public final BlockCollection structure;
	
	/**
	 * Whether air blocks will be placed.
	 */
	public boolean placeAir = false;
	
	/**
	 * Offset from execute location to spawn at.
	 */
	public Vector3i offset = new Vector3i(0, 0, 0);
	
	public StructureFunction(String namespace, String path, BlockCollection structure) {
		super(namespace, path);
		this.structure = structure;
	}
	
	public StructureFunction(Identifier identifier, BlockCollection structure) {
		super(identifier.namespace, identifier.value);
		this.structure = structure;
	}

	@Override
	public List<Command> getCommands() {
		List<Command> commands = new ArrayList<>();
		for (Vector3i pos : structure) {
			Block block = structure.blockAt(pos);
			if (block == null || (!placeAir && block.getName().equals("minecraft:air"))) {
				continue;
			}
			CommandVector3i finalPos = new CommandVector3i(pos.add(offset), Mode.RELATIVE);
			commands.add(new SetBlockCommand(finalPos, new BlockArguement(block, structure.blockEntityAt(pos)), SetBlockCommand.Mode.REPLACE));
		}
		
		return commands;
	}

}
