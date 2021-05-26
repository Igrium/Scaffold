package org.scaffoldeditor.scaffold.level;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.nbt.NBTStrings;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.block.Section;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileEndStatus;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileResult;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.WorldUpdates.WorldUpdateEvent;
import org.scaffoldeditor.scaffold.level.WorldUpdates.WorldUpdateListener;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.game.TargetSelectable;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.MCFunction;
import org.scaffoldeditor.scaffold.math.Vector;
import org.scaffoldeditor.scaffold.operation.OperationManager;
import org.scaffoldeditor.scaffold.serialization.LevelReader;
import org.scaffoldeditor.scaffold.serialization.LevelWriter;

import net.querz.nbt.tag.CompoundTag;

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
	
	/** Sections that have uncompiled changes. */
	public final Set<SectionCoordinate> dirtySections = new HashSet<>();
	
	/** Whether the level should automatically recompile the relevent chunks when a block entity is updated. */
	public boolean autoRecompile = true;
	
	private boolean hasUnsavedChanges = false;
	
	private List<WorldUpdateListener> worldUpdateListeners = new ArrayList<>();
	private List<Runnable> updateStackListeners = new ArrayList<>();
	
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
	
	public void setInitFunction(MCFunction initFunction) {
		this.initFunction = initFunction;
	}
	
	/**
	 * For use in the compiler only.
	 */
	public void setTickFunction(MCFunction tickFunction) {
		this.tickFunction = tickFunction;
	}
	
	/**
	 * For use in the compiler only.
	 */
	public void setDatapack(Datapack datapack) {
		this.datapack = datapack;
	}
	
	/**
	 * Make sure an entity name is valid (there are no duplicates), and return a new,
	 * valid name if it isn't.
	 * @param name Name to test.
	 * @param ignore If the name is in this list, return it as-is.
	 * @return Valid name.
	 */
	public String validateName(String name, String[] ignore) {
		if (Arrays.asList(ignore).contains(name)) {
			return name;
		}
		
		while (entities.containsKey(name)){
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
		entity.setShouldRender(true);
		
		entities.put(name, entity);
		entityStack.add(name);
		updateEntityStack();
		
		return entity;
	}
	
	/**
	 * Add an existing entity object to the level.
	 * <br>
	 * <b>Warning: </b> Only call if you know what you're doing! Can lead to illegal states!
	 * <code>newEntity</code> should be used most of the time.
	 * @param entity Entity to add.
	 */
	public void addEntity(Entity entity) {
		addEntity(entity, entityStack.size(), false);
	}
	
	/**
	 * Add an existing entity object to the level.
	 * <br>
	 * <b>Warning: </b> Only call if you know what you're doing! Can lead to illegal states!
	 * <code>newEntity</code> should be used most of the time.
	 * @param entity Entity to add.
	 * @param stackIndex Index to add it to in the entity stack.
	 * @param noRecompile Don't recompile the level afterward.
	 */
	public void addEntity(Entity entity, int stackIndex, boolean noRecompile) {
		entity.setName(validateName(entity.getName(), new String[] {}));
		
		entities.put(entity.getName(), entity);
		entityStack.add(stackIndex, entity.getName());
		updateEntityStack();
		entity.setShouldRender(true);
		
		if (entity instanceof BlockEntity) {
			dirtySections.addAll(((BlockEntity) entity).getOverlappingSections(blockWorld));
		}
		
		if (!noRecompile && autoRecompile) {
			quickRecompile();
		}
	}
	
	/**
	 * Remove an entity from the level.
	 * @param name Entity to remove.
	 */
	public void removeEntity(String name) {
		removeEntity(name, false);
	}
	
	/**
	 * Remove an entity from the level.
	 * @param name Entity to remove.
	 * @param noRecompile Don't recompile the level after removal.
	 */
	public void removeEntity(String name, boolean noRecompile) {
		Entity entity = entities.get(name);
		entity.setShouldRender(false);
		if (entity instanceof BlockEntity) {
			dirtySections.addAll(((BlockEntity) entity).getOverlappingSections(blockWorld));
		}
		
		entities.remove(name);
		entityStack.remove(name);
		updateEntityStack();
		
		if (!noRecompile && autoRecompile) {
			quickRecompile();
		}
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
		updateEntityStack();
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
	
	
	@SuppressWarnings("deprecation")
	public String summonScoreboardEntity() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("CustomName","\""+SCOREBOARDNAME+"\"");
		nbt.putInt("Duration", 2000000000);
		
		return "summon "+SCOREBOARDTYPE+" 0 0 0 "+NBTStrings.nbtToString(nbt);
	}
	
	/**
	 * Save this level to a file
	 * @param file File to save to
	 * @return Success
	 */
	public boolean saveFile(File file) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			new LevelWriter(out).write(this);
			out.close();
			setHasUnsavedChanges(false);
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
		dirtyChunks.clear();
		dirtySections.clear();
		
		return true;
	}
	
	/**
	 * Compile a specific set of chunks.
	 * Less efficient than <code>compileBlockWorld()</code> if compiling the entire world.
	 * @param chunks Chunks to compile.
	 */
	public void compileChunks(Set<ChunkCoordinate> chunks) {
		if (chunks.isEmpty()) {
			return;
		}
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
			return;
		}
		
		for (BlockEntity entity : updatingEntities) {
			entity.compileWorld(tempWorld, false);
		}
		
		for (ChunkCoordinate coord : tempWorld.getChunks().keySet()) {
			// Only save if the chunk is marked for update or it's not present in the main world.
			if (chunks.contains(coord) || !getBlockWorld().getChunks().keySet().contains(coord))
			blockWorld.getChunks().put(coord, tempWorld.getChunks().get(coord));
		}
		
		return;
	}
	
	/**
	 * Compile a specific set of sections. Less efficient than
	 * <code>compileBlockWorld()</code> and <code>compileChunks()</code> if
	 * compiling the entire world or an entire set of chunks.
	 * 
	 * @param sections Sections to compile.
	 */
	public void compileSections(Set<SectionCoordinate> sections) {
		if (sections.isEmpty()) {
			return;
		}
		// Compile into a temporary block world so other chunks don't get corrupted.
		BlockWorld tempWorld = new BlockWorld();
		
		List<BlockEntity> updatingEntities = new ArrayList<>();
		
		for (String entName : entityStack) {
			Entity entity = getEntity(entName);
			if (entity instanceof BlockEntity) {
				BlockEntity blockEntity = (BlockEntity) entity;
				// See if entity is within chunkList.
				for (SectionCoordinate section : sections) {
					Vector sectionStart = new Vector(section.x * Chunk.WIDTH, section.y * Section.HEIGHT, section.z * Chunk.LENGTH);
					Vector sectionEnd = new Vector(sectionStart.x + Chunk.WIDTH, section.y + Section.HEIGHT, section.z + Chunk.HEIGHT);
					
					if (blockEntity.overlapsVolume(sectionStart, sectionEnd)) {
						updatingEntities.add(blockEntity);
						break;
					}
				}
			}
		}
		
		if (updatingEntities.size() == 0) {
			return;
		}
		
		for (BlockEntity entity : updatingEntities) {
			entity.compileWorld(tempWorld, false);
		}
		
		// Make sure each chunk with sections marked for update is present in the temp world
		for (SectionCoordinate coord : sections) {
			ChunkCoordinate chunk = new ChunkCoordinate(coord);
			if (!tempWorld.getChunks().containsKey(chunk)) {
				tempWorld.getChunks().put(chunk, new Chunk());
			}
		}
		
		for (ChunkCoordinate coord : tempWorld.getChunks().keySet()) {
			// Only save if the chunk is marked for update or it's not present in the main world.
			if (!getBlockWorld().getChunks().keySet().contains(coord)) {
				blockWorld.getChunks().put(coord, tempWorld.getChunks().get(coord));
			} else {
				Chunk chunk = tempWorld.getChunks().get(coord);
				for (int y = 0; y < chunk.sections.length; y++) {
					if (sections.contains(new SectionCoordinate(coord.x(), y, coord.z()))) {
						 getBlockWorld().chunkAt(coord.x(), coord.z()).sections[y] = chunk.sections[y];
					}
				}
				
			}
//			if (sections.contains(coord) || !getBlockWorld().getChunks().keySet().contains(coord))
//			blockWorld.getChunks().put(coord, tempWorld.getChunks().get(coord));
		}
		
		return;
	}
	
	/**
	 * Compile all the chunks marked as dirty.
	 */
	public void quickRecompile() {
		compileSections(dirtySections);
		compileChunks(dirtyChunks);
		fireWorldUpdateEvent(dirtySections);
		dirtyChunks.clear();
		dirtySections.clear();
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
	
	protected void fireWorldUpdateEvent(Set<SectionCoordinate> sections) {
		for (WorldUpdateListener listener : worldUpdateListeners) {
			listener.onWorldUpdated(new WorldUpdateEvent(this, sections));
		}
	}
	
	public BlockWorld getBlockWorld() {
		return this.blockWorld;
	}
	
	/**
	 * Called when there is an update to the entity stack.
	 */
	public void onUpdateEntityStack(Runnable listener) {
		updateStackListeners.add(listener);
	}
	
	/**
	 * Trigger all entity stack listeners.
	 */
	public void updateEntityStack() {
		for (Runnable listener : updateStackListeners) {
			listener.run();
		}
	}

	
	/**
	 * Compile this level into a playable Minecraft world (UNFINISHED).
	 * @param compileTarget Folder to compile into (saves/name).
	 * @param cheats Should this level compile with cheats enabled?
	 * @return Success
	 */
	public boolean compile(Path compileTarget, boolean cheats) {
		Map<String, Attribute<?>> args = new HashMap<>();
		args.put("cheats", new BooleanAttribute(cheats));
		
		CompileResult result = project.getCompiler().compile(this, compileTarget, args, null);
		return result.endStatus == CompileEndStatus.FINISHED;
	}
	
	public boolean compile(Path compileTarget) {
		return compile(compileTarget, false);
	}
	
	public boolean hasUnsavedChanges() {
		return hasUnsavedChanges;
	}

	public void setHasUnsavedChanges(boolean hasUnsavedChanges) {
		this.hasUnsavedChanges = hasUnsavedChanges;
	}
		
}
