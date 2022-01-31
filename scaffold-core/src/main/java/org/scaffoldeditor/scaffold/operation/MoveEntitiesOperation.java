package org.scaffoldeditor.scaffold.operation;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.util.ProgressListener;

public class MoveEntitiesOperation implements Operation<Void> {
	private Map<Entity, Vector3dc> targetPositions;
	private Map<Entity, Vector3dc> oldPositions;
	
	public MoveEntitiesOperation(Map<Entity, Vector3dc> targetPositions) {
		this.targetPositions = Map.copyOf(targetPositions);
	}

	@Deprecated
	public MoveEntitiesOperation(Map<Entity, Vector3dc> targetPositions, Level level) {
		this(targetPositions);
	}

	@Override
	public Void execute(ProgressListener listener) {
		oldPositions = new HashMap<>();
		targetPositions.keySet().stream().forEach(ent -> {
			oldPositions.put(ent, ent.getPosition());
		});
		
		move(targetPositions);
		return null;
	}

	@Override
	public void undo() {
		move(oldPositions);
	}

	@Override
	public void redo() {
		move(targetPositions);
	}
	
	private void move(Map<Entity, Vector3dc> targets) {
		for (Entity ent : targets.keySet()) {
			ent.setPosition(targets.get(ent));
		}
	}

	@Override
	public String getName() {
		return "Move Entities";
	}
}
