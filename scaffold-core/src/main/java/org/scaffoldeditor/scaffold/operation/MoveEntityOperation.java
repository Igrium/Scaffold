package org.scaffoldeditor.scaffold.operation;

import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.level.entity.Entity;

@Deprecated
public class MoveEntityOperation implements Operation {
	private Entity target;
	private Vector3dc newPosition;
	
	private Vector3dc oldPosition;
	
	public MoveEntityOperation(Entity target, Vector3dc newPosition) {
		this.target = target;
		this.newPosition = newPosition;
	}
	
	public MoveEntityOperation(Entity target, Vector3dc oldPosition, Vector3dc newPosition) {
		this.target = target;
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}

	@Override
	public boolean execute() {
		if (oldPosition == null) {
			oldPosition = target.getPosition();	
		}
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
