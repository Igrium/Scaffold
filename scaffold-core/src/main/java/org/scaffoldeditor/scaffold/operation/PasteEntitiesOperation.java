package org.scaffoldeditor.scaffold.operation;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.level.stack.StackItem;
import org.scaffoldeditor.scaffold.util.ClipboardManager;

public class PasteEntitiesOperation implements Operation {
	
	private Level level;
	private List<StackItem> pastedItems;
	private StackGroup parent;
	
	public PasteEntitiesOperation(Level level, StackGroup parent) {
		this.level = level;
		this.parent = parent;
	}

	@Override
	public boolean execute() {
		try {
			pastedItems = ClipboardManager.getInstance().paste(level, parent);
			return true;
		} catch (Throwable e) {
			LogManager.getLogger().error(e);
			return false;
		}
	}

	@Override
	public void undo() {
		parent.items.removeAll(pastedItems);
		
		for (Entity ent : new StackGroup(pastedItems, "")) {
			ent.onRemoved();
		}
		
		level.updateLevelStack();
	}

	@Override
	public void redo() {
		parent.items.addAll(pastedItems);
		
		for (Entity ent : new StackGroup(pastedItems, "")) {
			ent.onAdded();
		}
		
		level.updateLevelStack();
	}

	@Override
	public String getName() {
		return "Paste";
	}

}
