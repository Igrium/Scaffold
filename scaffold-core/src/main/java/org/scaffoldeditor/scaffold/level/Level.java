package org.scaffoldeditor.scaffold.level;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.scaffoldeditor.nbt.NBTStrings;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.scaffold.core.Constants;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.WorldUpdates.WorldUpdateEvent;
import org.scaffoldeditor.scaffold.level.WorldUpdates.WorldUpdateListener;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.game.TargetSelectable;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.MCFunction;
import org.scaffoldeditor.scaffold.logic.Resourcepack;
import org.scaffoldeditor.scaffold.math.Vector;
import org.scaffoldeditor.scaffold.serialization.LevelReader;
import org.scaffoldeditor.scaffold.serialization.LevelWriter;

import com.github.mryurihi.tbnbt.tag.NBTTag;
import com.github.mryurihi.tbnbt.tag.NBTTagCompound;
import com.github.mryurihi.tbnbt.tag.NBTTagInt;
import com.github.mryurihi.tbnbt.tag.NBTTagString;

import operation.OperationManager;

/**
 * Represents a single level file
 * @author Sam54123
 *
 */
public class Level {
	
	/* The name of the entity that keeps track of level scoreboard objectives */
	public static final String SCOREBOARDNAME = "worldspawn";
	
	/* The type of the entity that keeps track of level scoreboard objectives */
	public static final String SCOREBOARDTYPE = "minecraft:area_effect_cloud";
		
	/* The project this level belongs to */
	private Project project;
	
	/* All the entities in the map */
	private Map<String, Entity> entities = new HashMap<String, Entity>();
	
	/* The order of entities in the map */
	private List<String> entityStack = new ArrayList<>();
	
	private LevelData levelData = new LevelData(this);
	
	/* Game functions. ONLY EXIST DURING COMPILATION */
	private MCFunction initFunction;
	private MCFunction tickFunction;
	private Datapack datapack;
	
	private String name = "level";
	
	/* The name that shows up in the Minecraft world menu */
	private String prettyName = "Level";
	
	/* The BlockWorld that this level uses */
	private BlockWorld blockWorld = new BlockWorld();
	private OperationManager operationManager = new OperationManager(this);
	
	/** Chunks that have uncompiled changes. */
	public final Set<ChunkCoordinate> dirtyChunks = new HashSet<>();
	/** Whether the level should automatically recompile the relevent chunks when a block entity is updated. */
	public boolean autoRecompile = true;
	
	private List<WorldUpdateListener> worldUpdateListeners = new ArrayList<>();
	
	/**
	 * Create a new level
	 * @param project Project to create level in
	 */
	public Level(Project project) {
		this.project = project;
	}

	/**
	 * Create a new level
	 * @param project Project to create level in
	 * @param prettyName Level name.
	 */
	public Level(Project project, String prettyName) {
		this.project = project;
		setPrettyName(prettyName);
	}
	
	/**
	 * Get the Project the level is a part of
	 * @return Assigned project
	 */
	public Project getProject() {
		return project;
	}
	
	/**
	 * Returns a Map with all this level's entities
	 * @return Map Entities
	 */
	public Map<String, Entity> getEntities() {
		return entities;
	}
	
	public List<String> getEntityStack() {
		return entityStack;
	}
	
	/**
	 * Get an entity by name.
	 * @param name Entity name.
	 * @return Entity
	 */
	public Entity getEntity(String name) {
		return entities.get(name);
	}
	
	/**
	 * Get this level's LevelData.
	 * @return LevelData
	 */
	public LevelData levelData() {
		return levelData;
	}
	
	/**
	 * Get this map's name.
	 * @return Map name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set this map's name.
	 * Removes all spaces.
	 * @param name New name.
	 */
	public void setName(String name) {
		// Remove spaces
		this.name = name.replaceAll("\\s+","");
	}
	
	/**
	 * Set the name that this level shows up as in the world browser.
	 * @param name New name
	 */
	public void setPrettyName(String name) {
		prettyName = name;
	}
	
	/**
	 * Get the name that this level shows up as in the world browser.
	 * @return Pretty name
	 */
	public String getPrettyName() {
		return prettyName;
	}
	
	/**
	 * Get the level's operation manager.
	 * @return Operation manager.
	 */
	public OperationManager getOperationManager() {
		return operationManager;
	}
	
	/**
	 * Get this map's init function.
	 * ONLY EXISTS DURING COMPILATION!
	 * @return Init function.
	 */
	public MCFunction initFunction() {
		return initFunction;
	}
	
	/**
	 * Get this map's init function.
	 * ONLY EXISTS DURING COMPILATION!
	 * @return Tick function.
	 */
	public MCFunction tickFunction() {
		return tickFunction;
	}
	
	/**
	 * Get this map's datapack.
	 * ONLY EXISTS DURING COMPILATION!
	 * @return Datapack.
	 */
	public Datapack getDatapack() {
		return datapack;
	}
	
	protected String validateName(String name, String[] ignore) {
		if (Arrays.asList(ignore).contains(name)) {
			return name;
		}
		
		while (entities.get(name) != null) {
			// Attempt to increment number
			if (Character.isDigit(name.charAt(name.length() - 1))) {
				int lastNum = Character.getNumericValue(name.charAt(name.length() - 1)) + 1;
				name = name.substring(0, name.length() - 1) + lastNum;
			} else {
				name = name + '1';
			}
		}
		return name;
	}
	
	/**
	 * Create a new entitiy.
	 * @param typeName Type name of entity to spawn.
	 * @param name Name of entity.
	 * @param position Position of entity.
	 * @return Newly created entity.
	 */
	public Entity newEntity(String typeName, String name, Vector position) {
		// Make sure entity with name doesn't already exist
		name = validateName(name, new String[] {});
		
		Entity entity = EntityRegistry.createEntity(typeName, this, name);
		entity.setPosition(position);
		
		entities.put(name, entity);
		entityStack.add(name);
		
		return entity;
	}
	
	
	/**
	 * Rename an entity in the level.
	 * @param oldName Entity to rename.
	 * @param newName New name.
	 * @return Success
	 */
	public boolean renameEntity(String oldName, String newName) {
		// Make sure entity exists and name is available
		if (entities.get(oldName) == null) {
			return false;
		}
		
		newName = validateName(newName, new String[] {oldName});
		
		Entity ent = entities.get(oldName);
		entities.put(newName, ent);
		entities.remove(oldName);
		
		int stackIndex = entityStack.indexOf(oldName);
		entityStack.set(stackIndex, newName);
		
		ent.setName(newName);
		return true;
	}
	
	private TargetSelectable scoreboardEntity;
	
	/**
	 * Get the Minecraft entity used to store the level scoreboard.
	 * @return Scoreboard entity.
	 */
	public TargetSelectable getScoreboardEntity() {
		if (scoreboardEntity == null) {
			scoreboardEntity = new TargetSelectable() {
				@Override
				public String getTargetSelector() {
					return "@e[type="+SCOREBOARDTYPE+", name="+SCOREBOARDNAME+"]";
				}
			};
		}
		
		return scoreboardEntity;
	}
	
	
	private String summonScoreboardEntity() {
		NBTTagCompound nbt = new NBTTagCompound(new HashMap<String, NBTTag>());
		nbt.put("CustomName", new NBTTagString("\""+SCOREBOARDNAME+"\""));
		nbt.put("Duration", new NBTTagInt(2000000000));
		
		return "summon "+SCOREBOARDTYPE+" 0 0 0 "+NBTStrings.nbtToString(nbt);
	}
	
	/**
	 * Serialize this level into a JSONObject.
	 * @return Serialized level
	 */
	public JSONObject serialize() {
		JSONObject object = new JSONObject();
		
		object.put("editorVersion", Constants.VERSION);
		object.put("prettyName", getPrettyName());
		
		// Add all maps
		JSONObject entities = new JSONObject();
		
		for (String key : this.entities.keySet()) {
			entities.put(key, this.entities.get(key).serialize());
		}
		
		object.put("entities", entities);
		object.put("data", levelData.getData());
		
		return object;
	}
	
	/**
	 * Unserialize a level from a JSONObject.
	 * @param project Project the level should belong to.
	 * @param object Serialized level.
	 * @return Unserlailized level.
	 */
	public static Level unserialize(Project project, JSONObject object) {
		Level level = new Level(project);
		
		// Unserialize JSON
		try {		
			level.setPrettyName(object.optString("prettyName"));
			
			JSONObject entities = object.getJSONObject("entities");
			level.levelData = new LevelData(level, object.getJSONObject("data"));
			
			for (String key : entities.keySet()) {
				// Find entity class
				String className = entities.getJSONObject(key).getString("type");
				
				try {
					@SuppressWarnings("unchecked")
					Class<? extends Entity> entityClass = (Class<? extends Entity>) Class.forName(className);
					
					// Involk unserialize method
					level.entities.put(key, (Entity) entityClass.getMethod("unserialize",
							new Class[] {Level.class, String.class, JSONObject.class})
							.invoke(null, level, key, entities.getJSONObject(key)));
					
				} catch (ClassNotFoundException 
						| IllegalAccessException 
						| IllegalArgumentException 
						| InvocationTargetException 
						| NoSuchMethodException 
						| SecurityException e) {
					System.out.println("Unable to instantiate class "+className);
				}
			}
			
		} catch (JSONException e) {
			System.out.println("Improperly formatted level!");
			return null;
		}
		
		level.compileBlockWorld(false); // Initial compile on blockWorld.
		return level;
	}
	
	/**
	 * Save this level to a file
	 * @param file File to save to
	 * @return Success
	 */
	public boolean saveFile(File file) {
		try {
			if (file.exists()) {
				file.delete();
			}
			FileOutputStream out = new FileOutputStream(file);
			new LevelWriter(out).write(this);
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Save this level to a file
	 * @param file File to save to
	 * @return Success
	 */
	public boolean saveFile(String file) {
		return saveFile(project.assetManager().getAbsolutePath(file).toFile());
	}
	
	/**
	 * Load a level from a file.
	 * @param project Project to load into
	 * @param file File to load
	 * @return Loaded level
	 */
	public static Level loadFile(Project project, File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			return new LevelReader(in).read(project);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new AssertionError("Unable to load level "+file.getName(), e);
		}
	}
	
	/**
	 * Load a level from a file.
	 * @param project Project to load into
	 * @param file File to load
	 * @return Loaded level
	 */
	public static Level loadFile(Project project, String file) {
		return loadFile(project, project.assetManager().getAbsolutePath(file).toFile());
	}
	
	/**
	 * Compile the entire blockworld.
	 * @param full Should this be a full compile? If true, entities may run more complex algorithems.
	 * @return Success.
	 */
	public boolean compileBlockWorld(boolean full) {
		blockWorld.clear(); // Clear the blockworld of previous compiles.
		System.out.println("Compiling world...");
		
		for (String name : entityStack) {
			Entity entity = getEntity(name);
			if (entity instanceof BlockEntity) {
				BlockEntity blockEntity = (BlockEntity) entity;
				blockEntity.compileWorld(blockWorld, full);
			}
		}
		fireWorldUpdateEvent(new HashSet<>());
		
		return true;
	}
	
	/**
	 * Compile a specific set of chunks.
	 * Less efficient than <code>compileBlockWorld()</code> if compiling the entire world.
	 * @param chunks Chunks to compile.
	 * @return Success.
	 */
	public boolean compileChunks(Set<ChunkCoordinate> chunks) {
		// Compile into a temporary block world so other chunks don't get corrupted.
		BlockWorld tempWorld = new BlockWorld();
		
		List<BlockEntity> updatingEntities = new ArrayList<>();
		
		for (String entName : entityStack) {
			Entity entity = getEntity(entName);
			if (entity instanceof BlockEntity) {
				BlockEntity blockEntity = (BlockEntity) entity;
				// See if entity is within chunkList.
				for (ChunkCoordinate chunk : chunks) {
					float[] chunkStart = new float[] { chunk.x() * Chunk.WIDTH, chunk.z() * Chunk.LENGTH };
					float[] chunkEnd = new float[] { chunkStart[0] + Chunk.WIDTH, chunkStart[1] + Chunk.LENGTH };
					
					if (blockEntity.overlapsArea(chunkStart, chunkEnd)) {
						updatingEntities.add(blockEntity);
						break;
					}
				}
			}
		}
		
		if (updatingEntities.size() == 0) {
			return true;
		}
		
		for (BlockEntity entity : updatingEntities) {
			entity.compileWorld(tempWorld, false);
		}
		
		for (ChunkCoordinate coord : tempWorld.getChunks().keySet()) {
			blockWorld.getChunks().put(coord, tempWorld.getChunks().get(coord));
		}
		
		return true;
	}
	
	/**
	 * Compile all the chunks marked as dirty.
	 */
	public void quickRecompile() {
		compileChunks(dirtyChunks);
		fireWorldUpdateEvent(dirtyChunks);
		dirtyChunks.clear();
	}
	
	/**
	 * Mark a chunk as needing compiling.
	 */
	public void markChunkDirty(ChunkCoordinate chunk) {
		dirtyChunks.add(chunk);
	}
	
	/**
	 * Called when the world is recompiled.
	 */
	public void onWorldUpdate(WorldUpdateListener listener) {
		this.worldUpdateListeners.add(listener);
	}
	
	protected void fireWorldUpdateEvent(Set<ChunkCoordinate> chunks) {
		for (WorldUpdateListener listener : worldUpdateListeners) {
			listener.onWorldUpdated(new WorldUpdateEvent(this, chunks));
		}
	}
	
	public BlockWorld getBlockWorld() {
		return this.blockWorld;
	}
	
	/**
	 * Compile level logic
	 * @param dataPath Path to datapack folder
	 * @return success
	 */
	public boolean compileLogic(Path dataPath) {
		// Create tick and init functions
		initFunction = new MCFunction("init");
		tickFunction = new MCFunction("tick");
		
		// Create datapack
		datapack = new Datapack(project, name);
		datapack.functions.add(initFunction);
		datapack.functions.add(tickFunction);
		
		datapack.loadFunctions.add(datapack.formatFunctionCall(initFunction));
		datapack.tickFunctions.add(datapack.formatFunctionCall(tickFunction));
		
		// Respawn scoreboard entity.
		initFunction.addCommand("kill "+getScoreboardEntity().getTargetSelector());
		initFunction.addCommand(summonScoreboardEntity());
		
		// Compile entities
		for (String key : entities.keySet()) {
			entities.get(key).compileLogic(datapack);
		}
		
		// Compile datapack
		try {
			datapack.compile(dataPath.resolve(project.getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Setup appropriate folders and files for world compile.
	 * @param compileTarget Folder to compile into (saves/name).
	 * @param cheats Should this level compile with cheats enabled?
	 * @throws IOException If an IO exception occurs
	 */
	private void setupCompile(Path compileTarget, boolean cheats) throws IOException {
		// Delete folder if exists
		if (compileTarget.toFile().exists()) {
			FileUtils.deleteDirectory(compileTarget.toFile());
		}

		// Make world folder
		compileTarget.toFile().mkdir();
		
		// Compile levelData
		levelData.compileFile(compileTarget.resolve("level.dat").toFile(), cheats);
	}
	
	/**
	 * Compile this level into a playable Minecraft world (UNFINISHED).
	 * @param compileTarget Folder to compile into (saves/name).
	 * @param cheats Should this level compile with cheats enabled?
	 * @return Success
	 */
	public boolean compile(Path compileTarget, boolean cheats) {
		try {
			setupCompile(compileTarget, cheats);
			
			// Compile world
			compileBlockWorld(true);
			
			File regionFolder = compileTarget.resolve("region").toFile();
			regionFolder.mkdir();
			blockWorld.serialize(regionFolder, org.scaffoldeditor.nbt.Constants.DEFAULT_DATA_VERSION);
			
			// Setup and compile logic.
			Path datapackFolder = compileTarget.resolve("datapacks");
			datapackFolder.toFile().mkdir();
			if (!compileLogic(datapackFolder)) {
				return false;
			}
			
			// Compile resourcepack.
			Resourcepack resourcepack = new Resourcepack(getProject().assetManager().getAbsolutePath("assets"));
			resourcepack.setDescription("Resources for "+project.getTitle());
			resourcepack.compile(compileTarget.resolve("resources"), true);
			
			
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to compile level!");
			return false;
		}
		
	}
	
	public boolean compile(Path compileTarget) {
		return compile(compileTarget, false);
	}
	
	/**
	 * Compile level logic
	 * @param dataPath Path to datapack folder
	 * @return success
	 */
	public boolean compileLogic(String dataPath) {
		return compileLogic(project.assetManager().getAbsolutePath(dataPath));
	}
		
}
