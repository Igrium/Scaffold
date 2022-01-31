package org.scaffoldeditor.scaffold.operation;

import java.io.IOException;
import java.util.List;

import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.level.stack.StackItem;
import org.scaffoldeditor.scaffold.util.ClipboardManager;
import org.scaffoldeditor.scaffold.util.ProgressListener;

public class PasteEntitiesOperation implements Operation<List<StackItem>> {
	
	private Level level;
	private List<StackItem> pastedItems;
	private StackGroup parent;
	
	public PasteEntitiesOperation(Level level, StackGroup parent) {
		this.level = level;
		this.parent = parent;
	}

	@Override
	public List<StackItem> execute(ProgressListener listener) throws IOException {
		pastedItems = ClipboardManager.getInstance().paste(level, parent);
		return pastedItems;
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
