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
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.io.AssetManager;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.WorldUpdates.UpdateRenderEntitiesEvent;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EntityAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.level.entity.game.RedstoneListener;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.level.io.Output;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.sdoc.SDoc;
import org.scaffoldeditor.scaffold.util.AttributeHolder;
import org.scaffoldeditor.scaffold.util.event.EventListener;
import org.w3c.dom.Element;

/**
 * Base entity class in maps
 * @author Igrium
 *
 */
public abstract class Entity extends AttributeHolder {
	
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
	
	/**
	 * When not empty, entity names within this entity (attributes and outputs) are
	 * remapped at compile time. Names not in this map will be treated as-is.
	 */
	public Map<String, String> entityNameOverride = new HashMap<>();
	
	/* Name of the entity */
	private String name;
	
	/* The level this entity belongs to */
	private Level level;
	
	@Attrib
	protected VectorAttribute position = new VectorAttribute(new Vector3f(0, 0, 0));
	
	private List<Output> outputs = new ArrayList<>();
		
	/**
	 * Construct a new entity with a name and a level.
	 * @param level	Level entity should belong to.
	 * @param name Entity name
	 */
	public Entity(Level level, String name) {
		this.name = name;
		this.level = level;
	}
	
	/**
	 * Obtain the macros (small functions that can be called from the UI) that this
	 * entity defines.
	 * 
	 * @return Macro list.
	 */
	public List<Macro> getMacros() {
		List<Macro> macros = new ArrayList<>();
		// macros.add(new Macro("Reset", () -> {
		// 	level.getOperationManager()
		// 			.execute(new ChangeAttributesOperation(this, getDefaultAttributes(), new ArrayList<>()));
		// }, new Confirmation("Reset all attributes?", "This will reset all attributes to their default values.")));
		return macros;
	}
	
	/**
	 * Compile an input on the entity. <br>
	 * <b>Warning:</b> Should not call <code>level#getDatapack()</code> or any other
	 * function that's only valid during logic compilation, as it'ss possible for
	 * this method to be called during world compilation (ex:
	 * {@link RedstoneListener}).
	 * 
	 * @param inputName  The name of the input being compiled.
	 * @param args       The arguements it's being compiled with.
	 * @param source     The entity that's compiling the input.
	 * @param instigator The "instigator" of this IO chain. Usually the entity that
	 *                   the function the commands will be written to belongs to.
	 * @return A list of all the commands that should be run when this input is
	 *         triggered.
	 */
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source, Entity instigator) {
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
		return compileOutput(outputName, this);
	}
	
	/**
	 * Compile all outputs with a trigger name matching the output name.
	 * 
	 * @param outputName Trigger name.
	 * @param instigator The "instigator" of this IO chain. Usually the entity that
	 *                   the function the commands will be written to belongs to.
	 * @return A list of all the commands that should be run when this output is
	 *         triggered.
	 */
	public List<Command> compileOutput(String outputName, Entity instigator) {
		List<Command> list = new ArrayList<>();
		
		for (Output output : outputs) {
			if (output.getTrigger().equals(outputName)) {
				list.addAll(output.compile(instigator));
			}
		}
		
		return list;
	}
	
	/**
	 * Determine an entity's compile-time name according to the {@link #entityNameOverride}.
	 * @param name Name to evaluate.
	 * @return Compile-time name. Might be the same as the original name.
	 */
	public String evaluateName(String name) {
		if (entityNameOverride.containsKey(name)) {
			name = entityNameOverride.get(name);
		}
		return name;
	}
	
	/**
	 * Replace all references to an entity name within this entity (attributes and outputs).
	 * @param original Original name.
	 * @param updated New name.
	 * @return How many instances of the name were found.
	 */
	public int refactorName(String original, String updated) {
		int found = 0;
		Map<String, Attribute<?>> newNames = new HashMap<>();
		for (String name : getAttributeNames()) {
			Attribute<?> att = getAttribute(name);
			if (att instanceof EntityAttribute && ((EntityAttribute) att).getValue().equals(original)) {
				newNames.put(name, new EntityAttribute(updated));
				found++;
			}
		}
		setAttributes(newNames);
		for (Output output : getOutputs()) {
			if (output.getTarget().equals(original)) {
				output.setTarget(updated);
				found++;
			}
		}
		
		return found;
	}
	
	/**
	 * Get this entity's name.
	 * @return Name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get this entity's compile-time name according to the {@link #entityNameOverride}.
	 * @return
	 */
	public String getFinalName() {
		return evaluateName(getName());
	}
	
	/**
	 * Set this entity's name.
	 * @param name New name
	 * @deprecated For internal use only. Use {@link Level#renameEntity(Entity, String, boolean)} instead.
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
		return position.getValue();
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
		if (isGridLocked()) position = position.floor().toFloat();
		this.setAttribute("position", new VectorAttribute(position));
	}

	/**
	 * Set the entity's position without triggering attribute updaters.
	 * @param position New position.
	 */
	protected void setPositionNoUpdate(Vector3f position) {
		if (isGridLocked()) position = position.floor().toFloat();
		this.position = new VectorAttribute(position);
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
	 * Shortcut method for getting the asset manager of the project this entity belongs to.
	 * @return Project's asset manager.
	 */
	public AssetManager getAssetManager() {
		return getProject().assetManager();
	}
	
	
	/**
	 * Get a map of this entity's attributes.
	 * @return Attributes
	 */
	@Deprecated
	protected Map<String, Attribute<?>> attributes() {
		return getAllAttributes();
	}
	
	/**
	 * Get a set of all this entity's attributes
	 * @return Attributes;
	 */
	public Set<String> getAttributes() {
		return getAttributeNames();
	}
	
	
	/**
	 * Set an attribute by name
	 * @param name Attribute name.
	 * @param value Attribute value.
	 * @param supressUpdate Don't call <code>onUpdateAttributes()</code>.
	 * This should be set when calling from the constructor.
	 * @deprecated <code>supressUpdate</code> doesn't do anything anymore.
	 */
	@Deprecated
	public void setAttribute(String name, Attribute<?> value, boolean supressUpdate) {
		setAttribute(name, value);
	}
	
	/**
	 * Remove an attribute from the entity.
	 * @param name Attribute to remove.
	 * @deprecated No difference between this and <code>setAttribute([name], null)</code>
	 */
	public void removeAttribute(String name) {
		setAttribute(name, null);
	}
	
	/**
	 * Reset this entity's attributes to default.
	 * 
	 * @param supressUpdate If true, {@link #onUpdateAttributes(boolean)} is NOT
	 *                      called.
	 */
	@Deprecated
	public void reset(boolean supressUpdate) {
		return;
	}

	/**
	 * Called when entity is deserialized for subclasses to act on. <br>
	 * <b>Note:</b> This is called before the entity is added to the level or any
	 * name conflict resolving.
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
	 * @deprecated <code>noRecompile</code> no-longer does anything as entities
	 *             never recompile the world themselves.
	 */
	@Deprecated
	public void onUpdateAttributes(boolean noRecompile) {
		updateRenderEntities();
	}
	
	/**
	 * <p>
	 * Called when the entity has finished initialization and is added (or re-added)
	 * to the level. This is when you should update your render entities.
	 * </p>
	 * <p>
	 * Note: if name refactoring has taken place during deserialization, this is
	 * called after this entity's name has changed but before it's refactored.
	 * </p>
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
	 * Whether the transform preview is enabled.
	 */
	protected boolean transformPreview = false;
	
	/**
	 * The current preview position.
	 */
	protected Vector3f previewPosition = new Vector3f(0,0,0);
	
	/**
	 * Get the position at which this entity should render in the editor. This is
	 * the function that should be used for positioning render entities.
	 * 
	 * @return The preview position if the transform preview is enabled, and the
	 *         standard position if it's not.
	 */
	public Vector3f getPreviewPosition() {
		if (transformPreview) return previewPosition;
		else return getPosition();
	}
	
	/**
	 * Enable the transform preview and set its position. Used for transform gismos
	 * and other operations where the standard implementation is too expensive to
	 * call every frame.
	 * 
	 * @param pos New preview position.
	 */
	public void setPreviewPosition(Vector3f pos) {
		transformPreview = true;
		previewPosition = pos;
		updateRenderEntities();
	}
	
	/**
	 * Disable the transform preview, reverting the rendered entity back to its real
	 * position.
	 */
	public void disableTransformPreview() {
		transformPreview = false;
		updateRenderEntities();
	}
	
	/**
	 * Determine whether this entity is grid-locked. Being grid-locked means it's a
	 * block entity or some other entity that must be aligned to the block grid.
	 * 
	 * @return Is this entity grid locked?
	 */
	public boolean isGridLocked() {
		return false;
	}
	
	/**
	 * Determine whether the transform preview is enabled.
	 * 
	 * @return True if this entity is actively being moved by a transformation gizmo
	 *         or the like.
	 */
	public boolean isTransformPreviewEnabled() {
		return transformPreview;
	}
	
	/**
	 * Compile this entity's logic.
	 * @param datapack Datapack to compile entity into
	 * @return Success
	 */
	public boolean compileLogic(Datapack datapack) {
		return true;
	}
	
	/**
	 * Get this entity's documentation. Usually this is loaded from an {@code sdoc}
	 * file using the asset manager.
	 * 
	 * @return Documentation object.
	 */
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/entity.sdoc", null);
	}
		
	@Override
	public String toString() {
		return getName();
	}
}
