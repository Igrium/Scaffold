package org.scaffoldeditor.scaffold.logic.datapack.constraints;

import java.util.List;

import org.scaffoldeditor.scaffold.entity.game.TargetSelectable;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;

/**
 * Represents a set of commands that, if called every tick, can constrain the
 * movement between two Minecraft entities.
 * 
 * @author Igrium
 */
public abstract class Constraint {
	/**
	 * The first entity in the constraint.
	 */
	public final TargetSelectable entity1;
	/**
	 * The second entity in the constraint.
	 */
	public final TargetSelectable entity2;
	
	public Constraint(TargetSelectable entity1, TargetSelectable entity2) {
		this.entity1 = entity1;
		this.entity2 = entity2;	
	}
	
	/**
	 * Get the commands that should be run every tick to apply this constraint.
	 * @return Commands.
	 */
	public abstract List<Command> getCommands();
}
