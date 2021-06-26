package org.scaffoldeditor.scaffold.operation;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.entity.BrushEntity;

public class ResizeBrushOperation implements Operation {
	
	private BrushEntity brush;
	private Vector3f[] oldSize;
	private Vector3f[] newSize;
	
	public ResizeBrushOperation(BrushEntity brush, Vector3f[] newSize) {
		this.brush = brush;
		this.newSize = newSize;
	}
	
	@Override
	public boolean execute() {
		oldSize = brush.getBrushBounds();
		brush.setBrushBounds(newSize, false);
		return true;
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
