package org.scaffoldeditor.scaffold.operation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.io.Output;
import org.scaffoldeditor.scaffold.util.ProgressListener;

/**
 * Update the attributes and/or outputs of an entity.
 * @author Igrium
 */
public class ChangeAttributesOperation implements Operation<Void> {
	
	private Entity target;
	private Map<String, Attribute<?>> attributes = new HashMap<>();
	private List<Output> outputs;
	private String newName;
	private boolean refactor;
	
	private Map<String, Attribute<?>> old = new HashMap<>();
	private List<Output> oldOutputs;
	private String oldName;
	
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
		this(target, attributes, outputs, null, false);
	}
	
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
	 * @param newName    Update to the entity's name.
	 * @param refactor   whether to refactor references to this entity. Only used if
	 *                   the name has been updated.
	 */
	public ChangeAttributesOperation(Entity target, Map<String, Attribute<?>> attributes, List<Output> outputs, String newName, boolean refactor) {
		this.target = target;
		this.attributes = attributes;
		this.outputs = outputs;
		this.newName = newName;
		this.refactor = refactor;
	}
	
	@Override
	public Void execute(ProgressListener listener) {
		// Ensure position is properly set on entity.
		if (attributes != null) {
			for (String name : attributes.keySet()) {		
				old.put(name, target.getAttribute(name));
			}
			target.setAttributes(attributes);
		}
		
		if (outputs != null) {
			outputs = outputs.stream().map(output -> output.clone()).collect(Collectors.toList());
			oldOutputs = target.getOutputs();
			
			target.getOutputs().clear();
			target.getOutputs().addAll(outputs);
		}
		
		if (newName != null) {
			oldName = target.getName();
			target.getLevel().renameEntity(target, newName, !refactor);
		}

		return null;
	}

	@Override
	public void undo() {
		if (old != null) {
			target.setAttributes(old);
		}
		
		if (oldOutputs != null) {
			target.getOutputs().clear();
			target.getOutputs().addAll(oldOutputs);
		}
		
		
		if (oldName != null) {
			target.getLevel().renameEntity(target, oldName, !refactor);
		}
	}

	@Override
	public void redo() {
		if (attributes != null) {
			target.setAttributes(attributes);
		}
		
		if (outputs != null) {
			target.getOutputs().clear();
			target.getOutputs().addAll(outputs);
		}
		
		if (newName != null) {
			target.getLevel().renameEntity(target, newName, !refactor);
		}
	}

	@Override
	public String getName() {
		return "Change entity attributes";
	}

}
