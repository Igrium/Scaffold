package org.scaffoldeditor.scaffold.logic.datapack.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.nbt.util.Pair;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.BlockArguement;
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
	
	/**
	 * Parse an execute command.
	 * @param in Command to parse, excluding the {@code execute} and {@code run} statements.
	 * @return Parsed command builder.
	 * @throws IllegalArgumentException If the command is improperly formatted.
	 */
	public static ExecuteCommandBuilder parse(String in) throws IllegalArgumentException {
		// Split string into sections.
		String[] split = split(in);
		ExecuteCommandBuilder builder = new ExecuteCommandBuilder();
		
		int head = 0;
		try {
			while (head < split.length) {
				String name = split[head];
				head++;
				
				if (name.equals("align")) {
					builder.subCommands.add(new Align(split[head]));
					head++;
				} else if (name.equals("anchored")) {
					builder.subCommands.add(new Anchored(split[head]));
					head++;
				} else if (name.equals("as")) {
					builder.subCommands.add(new As(TargetSelector.fromString(split[head])));
					head++;
				} else if (name.equals("at")) {
					builder.subCommands.add(new At(TargetSelector.fromString(split[head])));
					head++;
				} else if (name.equals("facing")) {
					String arg = split[head];
					head++;
					if (arg.equals("entity")) {
						TargetSelector target = TargetSelector.fromString(split[head]);
						head++;
						String anchor = split[head];
						head++;
						builder.subCommands.add(new FacingEnt(target, anchor));
					} else {
						builder.subCommands.add(new Facing(parseVector(arg, split, head - 1)));
					}
				} else if (name.equals("in")) {
					builder.subCommands.add(new In(split[head]));
					head++;
				} else if (name.equals("positioned")) {
					String arg = split[head];
					head++;
					if (arg.equals("as")) {
						builder.subCommands.add(new PositionedAs(TargetSelector.fromString(split[head])));
						head++;
					} else {
						builder.subCommands.add(new Positioned(parseVector(arg, split, head - 1)));
					}
				} else if (name.equals("rotated")) {
					String arg = split[head];
					head++;
					if (arg.equals("as")) {
						builder.subCommands.add(new RotatedAs(TargetSelector.fromString(split[head])));
						head++;
					} else {
						try {
							builder.subCommands.add(new Rotated(CommandRotation.fromString(arg)));
						} catch (NumberFormatException e) {
							throw new IllegalArgumentException("Improperly formatted number: " + String.join(" ", Arrays.copyOfRange(split, 0, head)));
						}
					}
				} else if (name.equals("if")) {
					Pair<Conditional, Integer> val = parseConditional(split, head);
					head += val.getSecond();
					builder.subCommands.add(new If(val.getFirst()));
				} else if (name.equals("unless")) {
					Pair<Conditional, Integer> val = parseConditional(split, head);
					head += val.getSecond();
					builder.subCommands.add(new Unless(val.getFirst()));
				} else {
					throw new IllegalArgumentException("Unknown subcommand '" + name + "': "
							+ String.join(" ", Arrays.copyOfRange(split, 0, head)) + "<--");
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Not enough arguements: " + in + "<--");
		}

		return builder;
	}
	
	private static Pair<Conditional, Integer> parseConditional(String[] split, int head) throws IllegalArgumentException {
		int startHead = head;
		String type = split[head];
		head++;
		
		if (type.equals("block")) {
			CommandVector3i pos = parseIntVec(split[head], split, head);
			head++;
			
			String block = split[head];
			head++;
			
			return new Pair<>(new BlockConditional(pos, BlockArguement.fromString(block)), head - startHead);
		} else if (type.equals("blocks")) {
			CommandVector3i start = parseIntVec(split[head], split, head);
			head++;
			
			CommandVector3i end = parseIntVec(split[head], split, head);
			head++;
			
			CommandVector3i dest = parseIntVec(split[head], split, head);
			head++;
			
			String mode = split[head];
			head++;
			
			return new Pair<>(new BlocksConditional(start, end, dest, mode), head - startHead);
		} else if (type.equals("data") ) {
			String dataType = split[head];
			head++;
			
			if (dataType.equals("block")) {
				CommandVector3i pos = parseIntVec(split[head], split, head);
				head++;
				
				String path =  split[head];
				head++;
				
				return new Pair<>(new DataBlockConditional(pos, path), head - startHead);
			} else if (dataType.equals("entity")) {
				TargetSelector target = TargetSelector.fromString(split[head]);
				head++;
				
				String path =  split[head];
				head++;
				
				return new Pair<>(new DataEntityConditional(target, path), head - startHead);
			} else if (dataType.equals("storage")) {
				Identifier source = new Identifier(split[head]);
				head++;
				
				String path = split[head];
				head++;
				
				return new Pair<>(new DataStorageConditional(source, path), head - startHead);
			} else {
				throw new IllegalArgumentException("Unknown data type: '" + dataType + "' "
						+ String.join(" ", Arrays.copyOfRange(split, 0, head)) + "<--");
			}
		} else if (type.equals("entity")) {
			TargetSelector target = TargetSelector.fromString(split[head]);
			head++;
			
			return new Pair<>(new EntityConditional(target), head - startHead);
		} else if (type.equals("predicate")) {
			String target = split[head];
			head++;
			return new Pair<>(new PredicateConditional(target), head - startHead);
		} else if (type.equals("score")) {
			TargetSelector target = TargetSelector.fromString(split[head]);
			head++;
			String targetObj = split[head];
			head++;
			
			String op = split[head];
			head++;
			
			Conditional conditional;
			if (op.equals("matches")) {
				TargetSelector source = TargetSelector.fromString(split[head]);
				head++;
				String sourceObj = split[head];
				head++;
				conditional = new ScoreConditional(target, targetObj, op, source, sourceObj);
			} else {
				String range = split[head];
				head++;
				conditional = new ScoreMatchesConditional(target, targetObj, range);
			}
			
			return new Pair<>(conditional, head - startHead);
		} else {
			throw new IllegalArgumentException("Unknown conditional type: '" + type + "' "
					+ String.join(" ", Arrays.copyOfRange(split, 0, head)) + "<--");
		}
	}
	
	/**
	 * Parse a vector.
	 * @param in Substring.
	 * @param split Full split command.
	 * @param head Head index the string was at.
	 * @return
	 */
	private static CommandVector3f parseVector(String in, String[] split, int head) {
		try {
			return CommandVector3f.fromString(in);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Improperly formatted number: " + String.join(" ", Arrays.copyOfRange(split, 0, head + 1)) + "<--");
		}	
	}
	
	private static CommandVector3i parseIntVec(String in, String[] split, int head) {
		try {
			return CommandVector3i.fromString(in);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Improperly formatted integer: "
					+ String.join(" ", Arrays.copyOfRange(split, 0, head + 1)) + "<--");
		}	
	}
	
	private static String[] split(String in) throws IllegalArgumentException {
		List<String> tokens = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		
		int bracketIndex = 0;
		int curlyIndex = 0;
		
		for (char c : in.strip().toCharArray()) {
			if (c == '[') {
				bracketIndex ++;
			}
			if (c == ']') {
				bracketIndex--;
			}
			if (c == '{') {
				curlyIndex++;
			}
			if (c == '}') {
				curlyIndex--;
			}
			
			if (c == ' ' && bracketIndex < 1 && curlyIndex < 1) {
				tokens.add(sb.toString());
				sb.delete(0, sb.length());
			} else {
				sb.append(c);
			}
		}
		
		tokens.add(sb.toString());
		
		if (bracketIndex != 0 ||  curlyIndex != 0) {
			throw new IllegalArgumentException("Unbalenced brackets: " + in + "<--");
		}
		
		return tokens.toArray(new String[0]);
	}
}
