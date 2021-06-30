package org.scaffoldeditor.scaffold.operation;

import org.scaffoldeditor.scaffold.level.Level;

import net.querz.nbt.tag.CompoundTag;

public class ChangeLevelPropertiesOperation implements Operation {
	
	private Level level;
	
	private String prettyName;
	private CompoundTag data;
	
	private String oldName;
	private CompoundTag oldData;
	
	public ChangeLevelPropertiesOperation(Level level, String prettyName, CompoundTag data) {
		this.level = level;
		this.prettyName = prettyName;
		this.data = data.clone();
	}

	@Override
	public boolean execute() {
		oldName = level.getPrettyName();
		oldData = level.levelData().getData();
		
		redo();
		return true;
	}

	@Override
	public void undo() {
		level.setPrettyName(oldName);
		level.levelData().setData(oldData);
	}

	@Override
	public void redo() {
		level.setPrettyName(prettyName);
		level.levelData().setData(data);
	}

	@Override
	public String getName() {
		return "Change level properties";
	}

}
