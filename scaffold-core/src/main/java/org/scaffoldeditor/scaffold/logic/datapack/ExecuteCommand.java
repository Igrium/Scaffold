package org.scaffoldeditor.scaffold.logic.datapack;

import java.util.List;
import java.util.StringJoiner;

import org.scaffoldeditor.scaffold.logic.datapack.arguements.BlockArguement;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandRotation;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3f;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3i;

public class ExecuteCommand implements Command {
	
	public static interface SubCommand {
		public String get();
	}
	
	public static interface Conditional {
		public String get();
	}
	
	public static class Align implements SubCommand {
		private String value;
		public Align(String value) {
			this.value = value;
		}
		public String get() {
			return "align "+value;
		}	
	}
	
	public static class Anchored implements SubCommand {
		private String value;
		public Anchored(String value) {
			this.value = value;
		}
		public String get() {
			return "anchored "+value;
		}
	}
	
	public static class As implements SubCommand {
		private TargetSelector value;
		public As(TargetSelector value) {
			this.value = value;
		}
		public String get() {
			 return "as "+value.compile();
		}
	}
	
	public static class At implements SubCommand {
		private TargetSelector value;
		public At(TargetSelector value) {
			this.value = value;
		}
		public String get() {
			return "at "+value.compile();
		}
	}
	
	public static class Facing implements SubCommand {
		private CommandVector3f value;
		public Facing(CommandVector3f value) {
			this.value = value;
		}
		public String get() {
			return "facing "+value.getString();
		}
	}
	
	public static class FacingEnt implements SubCommand {
		private TargetSelector target;
		private String anchor;
		public FacingEnt(TargetSelector target, String anchor) {
			this.target = target;
			this.anchor = anchor;
		}
		public String get() {
			return "facing entity "+target.compile()+" "+anchor;
		}
	}
	
	public static class In implements SubCommand {
		private String value;
		public In(String value) {
			this.value = value;
		}
		public String get() {
			return "in "+value;
		}
	}
	
	public static class Positioned implements SubCommand {
		private CommandVector3f value;
		public Positioned(CommandVector3f value) {
			this.value = value;
		}
		public String get() {
			return "positioned "+value.getString();
		}
	}
	
	public static class PositionedAs implements SubCommand {
		private TargetSelector value;
		public PositionedAs(TargetSelector value) {
			this.value = value;
		}
		public String get() {
			return "positioned as "+value.compile();
		}
	}
	
	public static class Rotated implements SubCommand {
		CommandRotation value;
		public Rotated(CommandRotation value) {
			this.value = value;
		}
		public String get() {
			return "rotated "+value.getString();
		}
	}
	
	public static class RotatedAs implements SubCommand {
		TargetSelector value;
		public RotatedAs(TargetSelector value) {
			this.value = value;
		}
		public String get() {
			return "rotated as "+value.compile();
		}
	}
	
	public static class If implements SubCommand {
		Conditional value;
		public If(Conditional value) {
			this.value = value;
		}
		public String get() {
			return "if "+value.get();
		}
	}
	
	public static class Unless implements SubCommand {
		Conditional value;
		public Unless(Conditional value) {
			this.value = value;
		}
		public String get() {
			return "unless "+value.get();
		}
	}
	
	public static class BlockConditional implements Conditional {
		public final BlockArguement predicate;	
		public BlockConditional(BlockArguement predicate) {
			this.predicate = predicate;
		}
		public String get() {
			return "block "+predicate.compile();
		}
	}
	
	public static class BlocksConditional implements Conditional {
		public final CommandVector3i start;
		public final CommandVector3i end;
		public final CommandVector3i destination;
		public final String scanMode;	
		public BlocksConditional(CommandVector3i start, CommandVector3i end, CommandVector3i destination, String scanMode) {
			this.start = start;
			this.end = end;
			this.destination = destination;
			this.scanMode = scanMode;
		}
		public String get() {
			return "blocks "+start+" "+end+" "+destination+" "+scanMode;
		}
	}
	
	public static class DataBlockConditional implements Conditional {
		public final CommandVector3i pos;
		public final String path;
		public DataBlockConditional(CommandVector3i pos, String path) {
			this.pos = pos;
			this.path = path;
		}
		public String get() {
			return "data block "+pos.getString()+" "+path;
		}
	}
	
	public static class DataEntityConditional implements Conditional {
		public final TargetSelector target;
		public final String path;
		public DataEntityConditional(TargetSelector target, String path) {
			this.target = target;
			this.path = path;
		}
		public String get() {
			return "data entity "+target.compile()+" "+path;
		}
	}
	
	public static class DataStorageConditional implements Conditional {
		public final String source;
		public final String path;
		public DataStorageConditional(String source, String path) {
			this.source = source;
			this.path = path;
		}
		public String get() {
			return "data storage "+source+" "+path;
		}
	}
	
	public static class EntityConditional implements Conditional {
		public final TargetSelector target;
		public EntityConditional(TargetSelector target) {
			this.target = target;
		}
		public String get() {
			return "entity "+target.compile();
		}
	}
	
	public static class PredicateConditional implements Conditional {
		public final String predicate;
		public PredicateConditional(String predicate) {
			this.predicate = predicate;
		}
		public String get() {
			return "predicate "+predicate;
		}
	}
	
	public static class ScoreConditional implements Conditional {
		public final TargetSelector target;
		public final String targetObjective;
		public final String operator;
		public final TargetSelector source;
		public final String sourceObjective;

		public ScoreConditional(TargetSelector target, String targetObjective, String operator, TargetSelector source,
				String sourceObjective) {
			this.target = target;
			this.targetObjective = targetObjective;
			this.operator = operator;
			this.source = source;
			this.sourceObjective = sourceObjective;
		}
		
		public String get() {
			StringJoiner joiner = new StringJoiner(" ");
			joiner.add("score");
			joiner.add(target.compile());
			joiner.add(targetObjective);
			joiner.add(operator);
			joiner.add(source.compile());
			joiner.add(sourceObjective);
			return joiner.toString();
		}
	}
	
	public static class ScoreMatchesConditional implements Conditional {
		public final TargetSelector target;
		public final String targetObjective;
		public final String range;
		public ScoreMatchesConditional(TargetSelector target, String targetObjective, String range) {
			this.target = target;
			this.targetObjective = targetObjective;
			this.range = range;
		}
		public String get() {
			return "score "+target.compile()+" "+targetObjective+" matches "+range;
		}
	}
	
	public static class StoreBlock implements SubCommand {
		public final String storeType;
		public final CommandVector3i targetPos;
		public final String path;
		public final String type;
		public final double scale;
		public StoreBlock(String storeType, CommandVector3i targetPos, String path, String type, double scale) {
			this.storeType = storeType;
			this.targetPos = targetPos;
			this.path = path;
			this.type = type;
			this.scale = scale;
		}
		public String get() {
			return "store "+storeType+" block "+targetPos.getString()+" "+path+" "+type+" "+scale;
		}
	}
	
	public static class StoreBossbar implements SubCommand {
		public final String storeType;
		public final String id;
		public final String overrideMode;
		public StoreBossbar(String storeType, String id, String overrideMode) {
			this.storeType = storeType;
			this.id = id;
			this.overrideMode = overrideMode;
		}
		public String get() {
			return "store "+storeType+" bossbar "+id+" "+overrideMode;
		}
	}
	
	public static class StoreEntity implements SubCommand {
		public final String storeType;
		public final TargetSelector target;
		public final String path;
		public final String type;
		public final double scale;
		public StoreEntity(String storeType, TargetSelector target, String path, String type, double scale) {
			this.storeType = storeType;
			this.target = target;
			this.path = path;
			this.type = type;
			this.scale = scale;
		}
		public String get() {
			return "store "+storeType+" entity "+target.compile()+" "+path+" "+type+" "+scale;
		}
	}
	
	public static class StoreScore implements SubCommand {
		public final String storeType;
		public final TargetSelector targets;
		public final String objective;
		public StoreScore(String storeType, TargetSelector targets, String objective) {
			this.storeType = storeType;
			this.targets = targets;
			this.objective = objective;
		}
		public String get() {
			return "store "+storeType+" score "+targets.compile()+" "+objective;
		}
	}
	
	public static class StoreStorage implements SubCommand {
		public final String storeType;
		public final String target;
		public final String path;
		public final String type;
		public final double scale;
		public StoreStorage(String storeType, String target, String path, String type, double scale) {
			this.storeType = storeType;
			this.target = target;
			this.path = path;
			this.type = type;
			this.scale = scale;
		}
		public String get() {
			return "store "+storeType+" storage "+target+" "+path+" "+type+" "+scale;
		}
	}
	
	private List<SubCommand> subCommands;
	private Command runCommand;
	
	public ExecuteCommand(List<SubCommand> subCommands, Command runCommand) {
		this.subCommands = subCommands;
		this.runCommand = runCommand;
	}
	
	@Override
	public String compile() {
		StringJoiner joiner = new StringJoiner(" ");
		joiner.add("execute");
		
		for (SubCommand command : subCommands) {
			joiner.add(command.get());
		}
		
		if (runCommand != null) {
			joiner.add("run");
			joiner.add(runCommand.compile());
		}
		
		return joiner.toString();
	}

}
