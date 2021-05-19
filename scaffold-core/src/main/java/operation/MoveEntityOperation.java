package operation;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.math.Vector;

public class MoveEntityOperation implements Operation {
	private Entity target;
	private Vector newPosition;
	private Level level;
	
	private Vector oldPosition;
	
	public MoveEntityOperation(Entity target, Vector newPosition) {
		this.target = target;
		this.newPosition = newPosition;
		this.level = target.getLevel();
	}

	@Override
	public boolean execute() {
		oldPosition = target.getPosition();
		target.setPosition(newPosition);
		if (level.autoRecompile) level.quickRecompile();
		return true;
	}

	@Override
	public void undo() {
		target.setPosition(oldPosition);
		if (level.autoRecompile) level.quickRecompile();
	}
	
	@Override
	public void redo() {
		target.setPosition(newPosition);
		if (level.autoRecompile) level.quickRecompile();
	}

	@Override
	public String getName() {
		return "Move "+target.getName();
	}
	
}
