package org.scaffoldeditor.scaffold.operation;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;

public class MoveEntitiesOperation implements Operation {
	private Map<Entity, Vector3f> targetPositions;
	private Map<Entity, Vector3f> oldPositions;
	private Level level;
	
	public MoveEntitiesOperation(Map<Entity, Vector3f> targetPositions, Level level) {
		this.targetPositions = Map.copyOf(targetPositions);
		this.level = level;
	}

	@Override
	public boolean execute() {
		oldPositions = new HashMap<>();
		targetPositions.keySet().stream().forEach(ent -> {
			oldPositions.put(ent, ent.getPosition());
		});
		
		move(targetPositions);
		return true;
	}

	@Override
	public void undo() {
		move(oldPositions);
	}

	@Override
	public void redo() {
		move(targetPositions);
	}
	
	private void move(Map<Entity, Vector3f> targets) {
		boolean recompileCache = level.autoRecompile;
		level.autoRecompile = false; // We only need to recompile once, regardless of how many entities were moved.
		for (Entity ent : targets.keySet()) {
			ent.setPosition(targets.get(ent));
		}
		level.autoRecompile = recompileCache;
		if (level.autoRecompile) {
			level.quickRecompile();
		}
	}

	@Override
	public String getName() {
		return "Move Entities";
	}
}
