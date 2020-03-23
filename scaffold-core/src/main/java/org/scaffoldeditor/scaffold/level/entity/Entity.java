package org.scaffoldeditor.scaffold.level.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.io.Input;
import org.scaffoldeditor.scaffold.level.io.Output;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.math.Vector;

/**
 * Base entity class in maps
 * @author Sam54123
 *
 */
public class Entity {
	
	/**
	 * Used to declare attribute types by name.
	 */
	public class AttributeDeclaration {
		private String name;
		private Class<? extends Object> type;
		
		public AttributeDeclaration(String name, Class<? extends Object> type) {
			this.name = name;
			this.type = type;
		}
		
		public String name() {
			return name;
		}
		public Class<? extends Object> type() {
			return type;
		}
	}
	
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
	
	/* Position of the entity in world space */
	private Vector position;
	
	/* Name of the entity */
	private String name;
	
	/* The level this entity belongs to */
	private Level level;
	
	/* All this entity's attributes */
	private Map<String, Object> attributes = new HashMap<String, Object>();
	
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
	 * Get a list of all the attribute fields.
	 * @return Attribute Fields.
	 */
	public List<AttributeDeclaration> getAttributeFields() {
		return new ArrayList<AttributeDeclaration>();
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
		return position;
	}
	
	/**
	 * Set this entity's world position.
	 * Called on entity creation.
	 * @param position New position
	 */
	public void setPosition(Vector position) {
		this.position = position;
	}
	
	/**
	 * Get the level this entity belongs to.
	 * @return Level
	 */
	public Level getLevel() {
		return level;
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
	protected Map<String, Object> attributes() {
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
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
		onUpdateAttributes();
	}
	
	
	/**
	 * Get an attribute by name
	 * @param name Attribute
	 */
	public Object getAttribute(String name) {
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
	 * Serialize this entity into a JSON object.
	 * @return Serialized entity
	 */
	public JSONObject serialize() {
		// Create object
		JSONObject object = new JSONObject();
		
		// Basic information
		object.put("type", getClass().getName());
		object.put("position", position.toJSONArray());
		
		// Attributes
		JSONObject attributeObject = new JSONObject();
		
		for (String key : attributes.keySet()) {
			if (attributes.get(key) != null) {
				attributeObject.put(key, attributes.get(key));
			}
		}
		
		object.put("attributes", attributeObject);
		
		JSONArray outputs = new JSONArray();
		for (Output o : this.outputs) {
			outputs.put(o.serialize());
		}
		object.put("outputs", outputs);
		
		return object;
	}

	
	/**
	 * Unserialize an entity fom a JSON object.
	 * @param level Level this entity should belong to
	 * @param name Name of the entity
	 * @param object JSON object to unserialize from
	 * @return Unserialized entity
	 */
	public static Entity unserialize(Level level, String name, JSONObject object) {
		try {
			// Create object	
			Class<?> entityType = Class.forName(object.getString("type"));
			Entity entity;
			
			if (!Entity.class.isAssignableFrom(entityType)) {
				System.out.println(entityType+
						" is not a subclass of org.metaversemedia.scaffold.level.entity.Entity!");
				return null;
			}
			
			entity = (Entity)
						entityType.getDeclaredConstructor(new Class[] {Level.class,String.class}).newInstance(level, name);
			
			
			// Basic info
			entity.setPosition(Vector.fromJSONArray(object.getJSONArray("position")));
			
			// Attributes
			JSONObject attributes = object.getJSONObject("attributes");
			
			for (String key : attributes.keySet()) {
				Object attribute = attributes.get(key);
				entity.attributes().put(key, attribute);
			}
			
			// Outputs
			JSONArray outputs = object.getJSONArray("outputs");
			for (Object o : outputs) {
				JSONObject outputJSON = (JSONObject) o;
				entity.outputs.add(Output.unserialize(outputJSON, entity));
				
			}
			
			entity.onUpdateAttributes();
			entity.onUnserialized(object);
			return entity;
		} catch (JSONException e) {
			System.out.println("Improperly formatted entity: "+name);
			return null;
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Unknown class: "+object.getString("type"));
			return null;
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Called when entity is unserialized for subclasses to act on.
	 * @param object JSONObject serialized from.
	 */
	protected void onUnserialized(JSONObject object) {}
	
	/**
	 * Called whenever any attributes are updated for subclasses to act on.
	 * This is called once on unserialization, before onUnserialized, and again whenever setAttribute() is called.
	 */
	protected void onUpdateAttributes() {}
	
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
}
