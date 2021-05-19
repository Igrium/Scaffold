package operation;

import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.math.Vector;

public class MoveEntityOperation implements Operation {
	private Entity target;
	private Vector newPosition;
	
	private Vector oldPosition;
	
	public MoveEntityOperation(Entity target, Vector newPosition) {
		this.target = target;
		this.newPosition = newPosition;
	}

	@Override
	public boolean execute() {
		oldPosition = target.getPosition();
		target.setPosition(newPosition);
		return true;
	}

	@Override
	public void undo() {
		target.setPosition(oldPosition);
	}
	
	@Override
	public void redo() {
		target.setPosition(newPosition);
	}

	@Override
	public String getName() {
		return "Move "+target.getName();
	}
	
}
