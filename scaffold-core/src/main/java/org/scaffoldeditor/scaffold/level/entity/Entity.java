package org.scaffoldeditor.scaffold.level.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.level.io.Input;
import org.scaffoldeditor.scaffold.level.io.Output;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.math.Vector;
import org.w3c.dom.Element;

/**
 * Base entity class in maps
 * @author Igrium
 *
 */
public class Entity {
	
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
	
	/* Whether non-block visualizations of this entity should render in the editor */
	private boolean shouldRender = false;
	
	/**
	 * Construct a new entity with a name and a level.
	 * @param level	Level entity should belong to.
	 * @param name Entity name
	 */
	public Entity(Level level, String name) {
		this.name = name;
		this.level = level;
		attributes().put("position", new VectorAttribute(new Vector(0, 0, 0)));
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
	 * Get the render type this entity should render with in the editor.
	 * SPRITE will render as a two dimensional sprite, and MODEL will render as a 3D model.
	 * @return Render type.
	 */
	public RenderType getRenderType() {
		return RenderType.SPRITE;
	}
	
	/**
	 * Get a path to the asset this entity should render with.
	 * If getRenderType() retruns SPRITE, this should be an image, and if it returns MODEL, this should be a model.
	 * @return
	 */
	public String getRenderAsset() {
		return "scaffold/textures/editor/billboard_generic.png";
	}
	
	/**
	 * Get the name the entity should have when it is first spawned into the editor.
	 * @return Default name.
	 */
	public String getDefaultName() {
		return getClass().getSimpleName().toLowerCase();
	}
	
	/**
	 * Get this entity's world position.
	 * @return Position
	 */
	public Vector getPosition() {
		return ((VectorAttribute) getAttribute("position")).getValue();
	}
	
	/**
	 * Set this entity's world position.
	 * Called on entity creation.
	 * @param position New position
	 */
	public void setPosition(Vector position) {
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
			onUpdateAttributes();
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
	
	/* All entitiy's outputs */
	private List<Output> outputs = new ArrayList<Output>();
	
	/**
	 * Get a list of all this entity's output connections.
	 * @return Outputs.
	 */
	public List<Output> outputConnections() {
		return outputs;
	}
	
	/**
	 * Get whether this entity has an output connection of the given name.
	 * @param name Name to check for.
	 * @return Has output connection?
	 */
	public boolean hasOutput(String name) {
		for (Output o : outputConnections()) {
			if (o.name.matches(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Create a new output.
	 * @param name Name of the output to trigger on.
	 * @return New output.
	 */
	public Output newOutputConnection(String name) {
		Output output = new Output(this);
		output.name = name;
		outputs.add(output);
		return output;
	}
	
	private Set<Input> inputs = new HashSet<Input>();
	
	/**
	 * Register a new input.
	 * @param input Input to register.
	 */
	protected void registerInput(Input input) {
		inputs.add(input);
	}
	
	/**
	 * Get a set of all this entity's inputs.
	 * @return Inputs.
	 */
	public Set<Input> getInputs() {
		return inputs;
	}
	
	/**
	 * Get an input my name.
	 * @param name Input name.
	 * @return Input.
	 */
	public Input getInput(String name) {
		for (Input e : inputs) {
			if (e.getName().matches(name)) {
				return e;
			}
		}
		return null;
	}
	/**
	 * Called when entity is deserialized for subclasses to act on.
	 * @param xml XML element that it was deserialized from.
	 */
	public void onUnserialized(Element xml) {}
	
	/**
	 * Called whenever any attributes are updated for subclasses to act on.
	 * This is called once on unserialization, before onUnserialized, and again whenever setAttribute() is called.
	 */
	public void onUpdateAttributes() {}
	
	/**
	 * Compile this entity's logic.
	 * @param datapack Datapack to compile entity into
	 * @return Success
	 */
	public boolean compileLogic(Datapack datapack) {
		return true;
	}
	
	/**
	 * Get the block pass of this entity.
	 * Entities with higher block passes will compile over entities with lower block passes.
	 * @return Block pass.
	 */
	public int getBlockPass() {
		return 0;
	}
	
	/**
	 * Compile an entity output into commands.
	 * 
	 * @param outputName Output name to compile.
	 * @param instigator Entity that started the io chain.
	 * @return Output commands.
	 */
	public String[] compileOutput(String outputName, Entity instigator) {
		// Get all outputs with name
		List<String> commands = new ArrayList<String>();
		for (Output o : outputConnections()) {
			if (o.name.matches(outputName)) {
				commands.add(o.compile(instigator));
			}
		}

		return commands.toArray(new String[0]);
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * Whether non-block visualizations of this entity should render in the editor.
	 */
	public boolean shouldRender() {
		return shouldRender;
	}
	
	/**
	 * Set whether non-block visualizations of this entity should render in the editor.
	 */
	public void setShouldRender(boolean shouldRender) {
		this.shouldRender = shouldRender;
	}
}
