package org.scaffoldeditor.scaffold.logic.datapack.commands;

import java.io.IOException;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3i;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

/**
 * Represents a Minecraft
 * <a href="https://minecraft.fandom.com/wiki/Commands/data">data</a> command.
 * 
 * @author Igrium
 *
 */
public class DataCommand implements Command {
	
	public enum Mode { MERGE, MODIFY, REMOVE }
	public enum TargetType { BLOCK, ENTITY, STORAGE }
	public enum ModificationType { APPEND, INSERT, MERGE, PREPEND, SET }
	public enum AppendMode { FROM, VALUE }
	
	public CommandVector3i targetPos;
	public TargetSelector targetEntity;
	public Identifier targetStorage;
	public CompoundTag nbt;
	public String targetPath;
	public int index = 0;
	public CommandVector3i sourcePos;
	public TargetSelector sourceEntity;
	public Identifier sourceStorage;
	public String sourcePath;
	public Tag<?> value;
	
	public Mode mode = Mode.MERGE;
	public TargetType target = TargetType.STORAGE;
	public ModificationType modification = ModificationType.MERGE;
	public TargetType source = TargetType.STORAGE;
	public AppendMode appendMode = AppendMode.VALUE;

	@Override
	public String compile() {
		try {
			if (mode == Mode.MERGE) {
				return "data merge "+compileTarget()+" "+SNBTUtil.toSNBT(nbt);
			} else if (mode == Mode.MODIFY) {
				String out = "data modify "+compileTarget()+" "+targetPath+" "+compileMod();
				if (appendMode == AppendMode.FROM) {
					out += " from "+compileSource();
					if (sourcePath != null) {
						return out+" "+sourcePath;
					} else {
						return out;
					}
				} else {
					return out+" value "+SNBTUtil.toSNBT(value);
				}
			} else {
				return "data remove "+compileTarget()+" "+targetPath;
			}
		} catch (IOException e) {
			throw new AssertionError("Error writing SNBT!", e);
		} catch (NullPointerException e) {
			throw new IllegalStateException("Not all required command arguements have been set!", e);
		}
	}
	
	private String compileTarget() {
		if (target == TargetType.BLOCK) {
			return "block "+targetPos.getString();
		} else if (target == TargetType.ENTITY) {
			return "entity "+targetEntity.compile();
		} else {
			return "storage "+targetStorage.toString();
		}
	}
	
	private String compileSource() {
		if (source == TargetType.BLOCK) {
			return "block "+sourcePos;
		} else if (source == TargetType.ENTITY) {
			return "entity "+sourceEntity.compile();
		} else {
			return "storage "+sourceStorage.toString();
		}
	}
	
	private String compileMod() {
		switch (modification) {
		case APPEND:
			return "append";
		case INSERT:
			return "insert " + index;
		case MERGE:
			return "merge";
		case PREPEND:
			return "prepend";
		case SET:
			return "set";
		default:
			throw new IllegalArgumentException("Modification type: " + modification + " is not valid!");
		}
	}

}
