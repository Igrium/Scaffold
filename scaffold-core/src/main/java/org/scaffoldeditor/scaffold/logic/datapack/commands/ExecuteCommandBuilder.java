package org.scaffoldeditor.scaffold.logic.datapack.commands;

import java.util.ArrayList;
import java.util.List;

import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandRotation;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3f;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3i;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.*;

/**
 * Builds execute commands.
 * @author Igrium
 */
public class ExecuteCommandBuilder {
	private List<SubCommand> subCommands = new ArrayList<>();
	
	public ExecuteCommandBuilder align(String axes) {
		subCommands.add(new Align(axes));
		return this;
	}
	
	public ExecuteCommandBuilder anchored(String anchor) {
		subCommands.add(new Anchored(anchor));
		return this;
	}
	
	public ExecuteCommandBuilder as(TargetSelector targets) {
		subCommands.add(new As(targets));
		return this;
	}
	
	public ExecuteCommandBuilder at(TargetSelector targets) {
		subCommands.add(new At(targets));
		return this;
	}
	
	public ExecuteCommandBuilder facing(CommandVector3f pos) {
		subCommands.add(new Facing(pos));
		return this;
	}
	
	public ExecuteCommandBuilder facingEntity(TargetSelector target, String anchor) {
		subCommands.add(new FacingEnt(target, anchor));
		return this;
	}
	
	public ExecuteCommandBuilder in(String dimension) {
		subCommands.add(new In(dimension));
		return this;
	}
	
	public ExecuteCommandBuilder positioned(CommandVector3f pos) {
		subCommands.add(new Positioned(pos));
		return this;
	}
	
	public ExecuteCommandBuilder positionedAs(TargetSelector targets) {
		subCommands.add(new PositionedAs(targets));
		return this;
	}
	
	public ExecuteCommandBuilder rotated(CommandRotation rot) {
		subCommands.add(new Rotated(rot));
		return this;
	}
	
	public ExecuteCommandBuilder rotatedAs(TargetSelector targets) {
		subCommands.add(new RotatedAs(targets));
		return this;
	}
	
	public ExecuteCommandBuilder executeIf(Conditional conditional) {
		subCommands.add(new If(conditional));
		return this;
	}
	
	public ExecuteCommandBuilder executeUnless(Conditional conditional) {
		subCommands.add(new Unless(conditional));
		return this;
	}
	
	public ExecuteCommandBuilder storeBlock(String storeType, CommandVector3i targetPos, String path, String type, double scale) {
		subCommands.add(new StoreBlock(storeType, targetPos, path, type, scale));
		return this;
	}
	
	public ExecuteCommandBuilder storeBossbar(String storeType, String id, String overrideMode) {
		subCommands.add(new StoreBossbar(storeType, id, overrideMode));
		return this;
	}
	
	public ExecuteCommandBuilder storeEntity(String storeType, TargetSelector targets, String path, String type, double scale) {
		subCommands.add(new StoreEntity(storeType, targets, path, type, scale));
		return this;
	}
	
	public ExecuteCommandBuilder storeScore(String storeType, TargetSelector targets, String objective) {
		subCommands.add(new StoreScore(storeType, targets, objective));
		return this;
	}
	
	public ExecuteCommandBuilder storeStorage(String storeType, String target, String path, String type, double scale) {
		subCommands.add(new StoreStorage(storeType, target, path, type, scale));
		return this;
	}
	
	public ExecuteCommand run(Command runCommand) {
		return new ExecuteCommand(List.copyOf(subCommands), runCommand);
	}
	
	public ExecuteCommand build() {
		return new ExecuteCommand(subCommands, null);
	}
	
	public List<SubCommand> getSubCommands() {
		return subCommands;
	}
}
