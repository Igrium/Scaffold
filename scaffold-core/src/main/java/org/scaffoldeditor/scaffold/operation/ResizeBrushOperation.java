package org.scaffoldeditor.scaffold.operation;

import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.level.entity.BrushEntity;
import org.scaffoldeditor.scaffold.util.ProgressListener;

public class ResizeBrushOperation implements Operation<Void> {
	
	private BrushEntity brush;
	private Vector3dc[] oldSize;
	private Vector3dc[] newSize;
	
	public ResizeBrushOperation(BrushEntity brush, Vector3dc[] newSize) {
		this.brush = brush;
		this.newSize = newSize;
	}
	
	@Override
	public Void execute(ProgressListener listener) {
		oldSize = brush.getBrushBounds();
		brush.setBrushBounds(newSize, false);
		return null;
	}

	@Override
	public void undo() {
		brush.setBrushBounds(oldSize, false);
	}

	@Override
	public void redo() {
		brush.setBrushBounds(newSize, false);
	}

	@Override
	public String getName() {
		return "Resize Brush";
	}

}
