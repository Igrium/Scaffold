package org.scaffoldeditor.scaffold.logic.datapack.commands;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3i;
import org.scaffoldeditor.scaffold.logic.datapack.commands.DataCommand.AppendMode;
import org.scaffoldeditor.scaffold.logic.datapack.commands.DataCommand.ModificationType;
import org.scaffoldeditor.scaffold.logic.datapack.commands.DataCommand.TargetType;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

public class DataCommandBuilder {
	private DataCommand builtCommand = new DataCommand();
	
	public DataMergeBuilder merge() {
		builtCommand.mode = DataCommand.Mode.MERGE;
		return new DataMergeBuilder();
	}
	
	public class DataMergeBuilder {
		
		public DataMergeBuilder block(CommandVector3i target) {
			builtCommand.target = TargetType.BLOCK;
			builtCommand.targetPos = target;
			return this;
		}
		
		public DataMergeBuilder entity(TargetSelector target) {
			builtCommand.target = TargetType.ENTITY;
			builtCommand.targetEntity = target;
			return this;
		}
		
		public DataMergeBuilder storage(Identifier target) {
			builtCommand.target = TargetType.STORAGE;
			builtCommand.targetStorage = target;
			return this;
		}
		
		public DataMergeBuilder nbt(CompoundTag nbt) {
			builtCommand.nbt = nbt;
			return this;
		}
		
		public DataCommand build() {
			return builtCommand;
		}
	}
	
	public DataRemoveBuilder remove() {
		builtCommand.mode = DataCommand.Mode.REMOVE;
		return new DataRemoveBuilder();
	}
	
	public class DataRemoveBuilder {
		
		public DataRemoveBuilder block(CommandVector3i target) {
			builtCommand.target = TargetType.BLOCK;
			builtCommand.targetPos = target;
			return this;
		}
		
		public DataRemoveBuilder entity(TargetSelector target) {
			builtCommand.target = TargetType.ENTITY;
			builtCommand.targetEntity = target;
			return this;
		}
		
		public DataRemoveBuilder storage(Identifier target) {
			builtCommand.target = TargetType.STORAGE;
			builtCommand.targetStorage = target;
			return this;
		}
		
		public DataRemoveBuilder path(String path) {
			builtCommand.targetPath = path;
			return this;
		}
		
		public DataCommand build() {
			return builtCommand;
		}
	}
	
	public DataModifyBuilder modify() {
		builtCommand.mode = DataCommand.Mode.MODIFY;
		return new DataModifyBuilder();
	}
	
	public class DataModifyBuilder {
		
		public DataModifyBuilder block(CommandVector3i target) {
			builtCommand.target = TargetType.BLOCK;
			builtCommand.targetPos = target;
			return this;
		}
		
		public DataModifyBuilder entity(TargetSelector target) {
			builtCommand.target = TargetType.ENTITY;
			builtCommand.targetEntity = target;
			return this;
		}
		
		public DataModifyBuilder storage(Identifier target) {
			builtCommand.target = TargetType.STORAGE;
			builtCommand.targetStorage = target;
			return this;
		}
		
		public DataModifyBuilder path(String path) {
			builtCommand.targetPath = path;
			return this;
		}
		
		public DataModifyBuilder append() {
			return modification(ModificationType.APPEND);
		}
		
		public DataModifyBuilder insert() {
			return modification(ModificationType.INSERT);
		}
		
		public DataModifyBuilder merge() {
			return modification(ModificationType.MERGE);
		}
		
		public DataModifyBuilder prepend() {
			return modification(ModificationType.PREPEND);
		}
		
		public DataModifyBuilder set() {
			return modification(ModificationType.SET);
		}
		
		public DataModifyBuilder modification(ModificationType modification) {
			builtCommand.modification = modification;
			return this;
		}
		
		public DataFromBuilder from() {
			builtCommand.appendMode = AppendMode.FROM;
			return new DataFromBuilder();
		}
		
		public class DataFromBuilder {
			public DataFromBuilder block(CommandVector3i target) {
				builtCommand.source = TargetType.BLOCK;
				builtCommand.sourcePos = target;
				return this;
			}
			
			public DataFromBuilder entity(TargetSelector target) {
				builtCommand.source = TargetType.ENTITY;
				builtCommand.sourceEntity = target;
				return this;
			}
			
			public DataFromBuilder storage(Identifier target) {
				builtCommand.source = TargetType.STORAGE;
				builtCommand.sourceStorage = target;
				return this;
			}
			
			public DataFromBuilder path(String path) {
				builtCommand.sourcePath = path;
				return this;
			}
			
			public DataCommand build() {
				return builtCommand;
			}
		}
		
		public DataModifyBuilder value(Tag<?> value) {
			builtCommand.appendMode = AppendMode.VALUE;
			builtCommand.value = value;
			return this;
		}
		
		public DataCommand build() {
			return builtCommand;
		}
	}
}
