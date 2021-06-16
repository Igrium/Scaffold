package org.scaffoldeditor.scaffold.operation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.level.io.Output;
import org.scaffoldeditor.scaffold.math.Vector;

/**
 * Update the attributes and/or outputs of an entity.
 * @author Igrium
 */
public class ChangeAttributesOperation implements Operation {
	
	private Entity target;
	private Map<String, Attribute<?>> attributes = new HashMap<>();
	private List<Output> outputs;
	private Vector newPosition = null;
	
	private Map<String, Attribute<?>> old = new HashMap<>();
	private List<Output> oldOutputs;
	private Vector oldPosition = null;
	
	/**
	 * Create a change attributes operation. <br>
	 * <b>Important:</b> if the changes include changes to mutable attributes, a
	 * copy of the attribute should be made. Do not change the attribute that's
	 * already assigned to the entity!
	 * 
	 * @param target     Entity to change the attributes of.
	 * @param attributes Attributes to change, or <code>null</code> if we're only
	 *                   changing outputs.
	 * @param outputs    New output set, or <code>null</code> if we're only changing
	 *                   attributes.
	 */
	public ChangeAttributesOperation(Entity target, Map<String, Attribute<?>> attributes, List<Output> outputs) {
		this.target = target;
		this.attributes = attributes;
		this.outputs = outputs;
	}
	
	@Override
	public boolean execute() {
		// Ensure position is properly set on entity.
		if (attributes != null) {
			if (attributes.containsKey("position")) {
				newPosition = ((VectorAttribute) attributes.get("position")).getValue();
				oldPosition = target.getPosition();
				attributes.remove("position");
				target.setPosition(newPosition);
			}
			
			for (String name : attributes.keySet()) {		
				old.put(name, target.getAttribute(name));
				target.setAttribute(name, attributes.get(name), true);
			}
			target.onUpdateAttributes(false);
		}
		if (outputs != null) {
			outputs = outputs.stream().map(output -> output.clone()).collect(Collectors.toList());
			oldOutputs = target.getOutputs();
			
			target.getOutputs().clear();
			target.getOutputs().addAll(outputs);
		}

		return true;
	}

	@Override
	public void undo() {
		if (old != null) {
			for (String name : old.keySet()) {
				target.setAttribute(name, old.get(name), true);
			}
			if (oldPosition != null) {
				target.setPosition(oldPosition);
			}
			target.onUpdateAttributes(false);
		}
		if (oldOutputs != null) {
			target.getOutputs().clear();
			target.getOutputs().addAll(oldOutputs);
		}
	}

	@Override
	public void redo() {
		if (attributes != null) {
			if (newPosition != null) {
				target.setPosition(newPosition);
			}
			for (String name : attributes.keySet()) {
				target.setAttribute(name, attributes.get(name), true);
			}
			target.onUpdateAttributes(false);
		}
		if (outputs != null) {
			target.getOutputs().clear();
			target.getOutputs().addAll(outputs);
		}
	}

	@Override
	public String getName() {
		return "Change entity attributes";
	}

}
