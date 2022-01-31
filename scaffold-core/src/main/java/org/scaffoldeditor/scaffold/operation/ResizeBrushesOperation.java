package org.scaffoldeditor.scaffold.operation;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.entity.BrushEntity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.util.ProgressListener;

public class ResizeBrushesOperation implements Operation<Void> {
	
	protected Map<BrushEntity, Vector3dc[]> newSizes;
	protected Map<BrushEntity, Vector3dc[]> oldSizes = new HashMap<>();
	protected Level level;
	
	public ResizeBrushesOperation(Map<BrushEntity, Vector3dc[]> newSizes, Level level) {
		this.newSizes = newSizes;
		this.level = level;
	}

	@Override
	public Void execute(ProgressListener listener) {
		for (BrushEntity ent : newSizes.keySet()) {
			oldSizes.put(ent, ent.getBrushBounds());
			ent.setBrushBounds(newSizes.get(ent), false);
		}

		return null;
	}

	@Override
	public void undo() {
		for (BrushEntity ent : oldSizes.keySet()) {
			ent.setBrushBounds(oldSizes.get(ent), false);
		}
	}

	@Override
	public void redo() {
		for (BrushEntity ent : newSizes.keySet()) {
			ent.setBrushBounds(newSizes.get(ent), false);
		}
	}

	@Override
	public String getName() {
		return "Resize Brushes";
	}

}
