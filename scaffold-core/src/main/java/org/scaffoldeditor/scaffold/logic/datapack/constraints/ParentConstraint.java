package org.scaffoldeditor.scaffold.logic.datapack.constraints;

import java.util.Arrays;
import java.util.List;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.entity.game.TargetSelectable;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector.Mode;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3f;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;

/**
 * A constraint that locks one entity to another in a parent-child relationship.
 * 
 * @author Igrium
 */
public class ParentConstraint extends Constraint {
	
	public final Vector3f offset;
	
	/**
	 * Create a parent constraint.
	 * @param entity1 Parent entity.
	 * @param entity2 Child entity.
	 * @param offset Offset of parent, in local space to parent entity.
	 */
	public ParentConstraint(TargetSelectable entity1, TargetSelectable entity2, Vector3f offset) {
		super(entity1, entity2);
		this.offset = offset;
	}

	@Override
	public List<Command> getCommands() {
		return Arrays.asList(new Command[] {
				new ExecuteCommandBuilder().at(entity1.getTargetSelector()).run(Command.fromString("teleport "
						+ entity2.getTargetSelector().compile() + " " + new CommandVector3f(offset, Mode.LOCAL).getString()))		
				});	
	}

}
