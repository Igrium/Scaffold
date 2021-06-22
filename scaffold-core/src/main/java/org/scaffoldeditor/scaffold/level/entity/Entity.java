package org.scaffoldeditor.scaffold.level.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.WorldUpdates.UpdateRenderEntitiesEvent;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.level.entity.game.RedstoneListener;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.level.io.Output;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.util.event.EventListener;
import org.w3c.dom.Element;

/**
 * Base entity class in maps
 * @author Igrium
 *
 */
public abstract class Entity {
	
	/**
	 * Special case used to declare file paths as attributes.
	 * Editor side only. Represented in entity as String.
	 */
	public class FileAttribute {
		public String getFileType() {
			return "";
		}
	}
	
	/**
	 * Used to tell the editor of this entity should render as a sprite or a model.
	 */
	public enum RenderType {
		SPRITE, MODEL
	}
	
	/**
	 * The type name this entity gets saved with when serialized.
	 */
	public String registryName;
	
	/* Name of the entity */
	private String name;
	
	/* The level this entity belongs to */
	private Level level;
	
	/* All this entity's attributes */
	private Map<String, Attribute<?>> attributes = new HashMap<>();
	
	private List<Output> outputs = new ArrayList<>();
		
	/**
	 * Construct a new entity with a name and a level.
	 * @param level	Level entity should belong to.
	 * @param name Entity name
	 */
	public Entity(Level level, String name) {
		this.name = name;
		this.level = level;
		attributes().put("position", new VectorAttribute(new Vector3f(0, 0, 0)));
		attributes.putAll(getDefaultAttributes());
	}
	
	/**
	 * Get the attributes this entity should have when it spawns.
	 * @return A map of the default attributes.
	 */
	public abstract Map<String, Attribute<?>> getDefaultAttributes();
	
	/**
	 * Compile an input on the entity. <br>
	 * <b>Warning:</b> Should not call <code>level#getDatapack()</code> or any other
	 * function that's only valid during logic compilation, as it's possible for
	 * this method to be called during world compilation (ex: {@link RedstoneListener}).
	 * 
	 * @param inputName The name of the input being compiled.
	 * @param args      The arguements it's being compiled with.
	 * @param source    The entity that's compiling the input.
	 * @return A list of all the commands that should be run when this input is
	 *         triggered.
	 */
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source) {
		return Collections.emptyList();
	};
	
	/**
	 * Get all the inputs that this entity accepts.
	 * @return A collection of input declarations.
	 */
	public Collection<InputDeclaration> getDeclaredInputs() {
		return new ArrayList<>();
	}
	
	/**
	 * Get all the outputs that this entity emits.
	 * @return A collection of output declarations.
	 */
	public Collection<OutputDeclaration> getDeclaredOutputs() {
		return new ArrayList<>();
	}
	
	/**
	 * Get all this entity's outputs that have been connected.
	 * @return A mutable list of this entity's outputs.
	 */
	public List<Output> getOutputs() {
		return outputs;
	}
	
	/**
	 * Compile all outputs with a trigger name matching the output name.
	 * 
	 * @param outputName Trigger name.
	 * @return A list of all the commands that should be run when this output is
	 *         triggered.
	 */
	public List<Command> compileOutput(String outputName) {
		List<Command> list = new ArrayList<>();
		
		for (Output output : outputs) {
			if (output.getTrigger().equals(outputName)) {
				list.addAll(output.compile());
			}
		}
		
		return list;
	}
	
	/**
	 * Get this entity's name.
	 * @return Name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set this entity's name. (DON'T CALL MANUALLY!)
	 * @param name New name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the name the entity should have when it is first spawned into the editor.
	 * @return Default name.
	 */
	public String getDefaultName() {
		return registryName;
	}
	
	/**
	 * Get this entity's world position.
	 * @return Position
	 */
	public Vector3f getPosition() {
		return ((VectorAttribute) getAttribute("position")).getValue();
	}
	
	/**
	 * Get the position of this entity on the block grid.
	 * @return Block position.
	 */
	public Vector3i getBlockPosition() {
		return getPosition().floor();
	}
	
	/**
	 * Set this entity's world position.
	 * Called on entity creation.
	 * @param position New position
	 */
	public void setPosition(Vector3f position) {
		this.setAttribute("position", new VectorAttribute(position));
	}
	
	/**
	 * Get the level this entity belongs to.
	 * @return Level
	 */
	public Level getLevel() {
		return level;
	}
	
	public BlockWorld getWorld() {
		return level.getBlockWorld();
	}
	
	/**
	 * Shortcut method for getting the project this entity belongs to.
	 * <br>
	 * Same as <code>getLevel().getProject()</code>
	 * @return The project this entity belongs to.
	 */
	public Project getProject() {
		return getLevel().getProject();
	}
	
	/**
	 * Get a map of this entity's attributes.
	 * @return Attributes
	 */
	protected Map<String, Attribute<?>> attributes() {
		return attributes;
	}
	
	/**
	 * Get a set of all this entity's attributes
	 * @return Attributes;
	 */
	public Set<String> getAttributes() {
		return attributes.keySet();
	}
	
	
	/**
	 * Set an attribute by name
	 * @param name Attribute name.
	 * @param value Attribute value.
	 */
	public void setAttribute(String name, Attribute<?> value) {
		setAttribute(name, value, false);
	}
	
	/**
	 * Set an attribute by name
	 * @param name Attribute name.
	 * @param value Attribute value.
	 * @param supressUpdate Don't call <code>onUpdateAttributes()</code>.
	 * This should be set when calling from the constructor.
	 */
	public void setAttribute(String name, Attribute<?> value, boolean supressUpdate) {
		attributes.put(name, value);
		if (!supressUpdate) {
			onUpdateAttributes(false);
		}
	}
	
	
	/**
	 * Get an attribute by name
	 * @param name Attribute
	 */
	public Attribute<?> getAttribute(String name) {
		return attributes.get(name);
	}
	
	/**
	 * Remove an attribute from the entity.
	 * @param name Attribute to remove.
	 */
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	/**
	 * Called when entity is deserialized for subclasses to act on. <br>
	 * <b>Note:</b> This may be called before the entity is added to the level.
	 * 
	 * @param xml XML element that it was deserialized from.
	 */
	public void onUnserialized(Element xml) {}
	
	/**
	 * Called whenever any attributes are updated for subclasses to act on. This is
	 * called once on unserialization, before onUnserialized, and again whenever
	 * setAttribute() is called.
	 * 
	 * @param noRecompile If this is true, this entity shouldn't recompile the
	 *                    world. Usually set when the calling function plans to
	 *                    recompile the world later.
	 */
	public void onUpdateAttributes(boolean noRecompile) {
		updateRenderEntities();
	}
	
	/**
	 * Called when the entity has finished initialization and is added (or re-added)
	 * to the level. This is when you should update your render entities.
	 */
	public void onAdded() {
		updateRenderEntities();
	}
	
	/**
	 * Get a set of all this entity's render entities. Called whenever they need to be updated.
	 * @return Render entities.
	 */
	public Set<RenderEntity> getRenderEntities() {
		return new HashSet<>();
	};
	
	
	/**
	 * <p>
	 * Called when the entity is removed from the level for any reason. This is NOT
	 * called when the level is closed. <br>
	 * <b>Note:</b> The entity may be re-added at any time, for instance, if the
	 * operation that removed it is undone.
	 * </p>
	 * <p>
	 * The default implementation fires the <code>onUpdateRenderEntities</code> event,
	 * causing the editor to remove its render entities.
	 */
	public void onRemoved() {
		updateRenderEntities(Collections.emptySet());
	}
	
	/**
	 * Tell the editor to update this entity's (non-block) visual representation.
	 * @param newRenderEntities New set of render entities.
	 */
	protected void updateRenderEntities(Set<RenderEntity> newRenderEntities) {
		level.fireUpdateRenderEntitiesEvent(new UpdateRenderEntitiesEvent(newRenderEntities, this));
	}
	
	/**
	 * Register a listener to be called when one {@link RenderEntity} or more have
	 * updated. If a render entity that previously existed isn't present in the
	 * list, it should be removed.
	 * 
	 * @param listener Event listener.
	 */
	public void onUpdateRenderEntities(EventListener<UpdateRenderEntitiesEvent> listener) {
		level.onUpdateRenderEntities(event -> {
			if (event.subject == this) {
				listener.eventFired(event);
			}
		});
	}
	
	/**
	 * Force-update this entity's visual representation in the editor.
	 */
	public void updateRenderEntities() {
		updateRenderEntities(getRenderEntities());
	}
	
	/**
	 * Compile this entity's logic.
	 * @param datapack Datapack to compile entity into
	 * @return Success
	 */
	public boolean compileLogic(Datapack datapack) {
		return true;
	}

	
	@Override
	public String toString() {
		return getName();
	}
}
