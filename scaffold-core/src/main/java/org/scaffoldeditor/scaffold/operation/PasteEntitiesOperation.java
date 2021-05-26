package org.scaffoldeditor.scaffold.operation;

import java.util.Set;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.util.ClipboardManager;

public class PasteEntitiesOperation implements Operation {
	
	private Level level;
	private Set<Entity> pastedEntities;
	
	public PasteEntitiesOperation(Level level) {
		this.level = level;
	}

	@Override
	public boolean execute() {
		try {
			pastedEntities = ClipboardManager.getInstance().pasteEntities(level);
			return true;
		} catch (AssertionError e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void undo() {
		for (Entity ent : pastedEntities) {
			level.removeEntity(ent.getName(), true);
		}
		if (level.autoRecompile) {
			level.quickRecompile();
		}
	}

	@Override
	public void redo() {
		for (Entity ent : pastedEntities) {
			level.addEntity(ent, level.getEntityStack().size(), true);
		}
		if (level.autoRecompile) {
			level.quickRecompile();
		}
	}

	@Override
	public String getName() {
		return "Paste Entities";
	}

}
