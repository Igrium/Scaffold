package org.scaffoldeditor.scaffold.level;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileEndStatus;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileResult;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.WorldUpdates.UpdateRenderEntitiesEvent;
import org.scaffoldeditor.scaffold.level.WorldUpdates.WorldUpdateEvent;
import org.scaffoldeditor.scaffold.level.WorldUpdates.WorldUpdateListener;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityProvider;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.level.entity.game.TargetSelectable;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.level.stack.StackItem;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.datapack.Function;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.operation.AddGroupOperation;
import org.scaffoldeditor.scaffold.operation.OperationManager;
import org.scaffoldeditor.scaffold.serialization.LevelReader;
import org.scaffoldeditor.scaffold.serialization.LevelWriter;
import org.scaffoldeditor.scaffold.util.LevelOperations;
import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;

/**
 * Represents a single level file
 * @author Igrium
 *
 */
public class Level {
	
	/* The name of the entity that keeps track of level scoreboard objectives */
	public static final String SCOREBOARDNAME = "worldspawn";
	
	/* The type of the entity that keeps track of level scoreboard objectives */
	public static final String SCOREBOARDTYPE = "minecraft:marker";
		
	/* The project this level belongs to */
	private Project project;
	
	/* All the entities in the map */
	private StackGroup levelStack = new StackGroup("entities");
	
	private LevelData levelData;
	
	/* Game functions. ONLY EXIST DURING COMPILATION */
	private Function initFunction;
	private Function tickFunction;
	private Datapack datapack;
	
	private String name = "level";
	
	/* The name that shows up in the Minecraft world menu */
	private String prettyName = "Level";
	
	private boolean enableResourcepack = false;
	
	/* The BlockWorld that this level uses */
	private BlockWorld blockWorld = new BlockWorld();
	private OperationManager operationManager = new OperationManager(this);
	
	/** Sections that have uncompiled changes. */
	public final Set<SectionCoordinate> dirtySections = new HashSet<>();
	
	/** Whether the level should automatically recompile the relevent chunks when a block entity is updated. */
	public boolean autoRecompile = true;
	
	private boolean hasUnsavedChanges = false;
	
	private List<WorldUpdateListener> worldUpdateListeners = new ArrayList<>();
	private List<Runnable> updateStackListeners = new ArrayList<>();
	private EventDispatcher<UpdateRenderEntitiesEvent> updateRenderEntitiesDispatcher = new EventDispatcher<>();
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Create a new level
	 * @param project Project to create level in
	 */
	public Level(Project project) {
		this.project = project;
		levelData = new LevelData(this);
	}

	/**
	 * Create a new level
	 * @param project Project to create level in
	 * @param prettyName Level name.
	 */
	public Level(Project project, String prettyName) {
		this.project = project;
		setPrettyName(prettyName);
		levelData = new LevelData(this);
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
	 * 
	 * @return Map Entities
	 * @deprecated With the addition of the level stack, and the change to the
	 *             structure in which entities are stored, this method is redundant.
	 *             It simply constructs an unmodifiable map from the data in the
	 *             level stack.
	 */
	public Map<String, Entity> getEntities() {
		Map<String, Entity> entities = new HashMap<>();
		for (Entity ent : levelStack) {
			entities.put(ent.getName(), ent);
		}
		
		return Map.copyOf(entities);
	}
	
	/**
	 * Get all the entities in the level, orginized into groups.
	 * @return Level stack.
	 */
	public StackGroup getLevelStack() {
		return levelStack;
	}
	
	/**
	 * Replace the level stack.
	 * @param newStack New stack
	 * @deprecated For internal use only. Can VERY EASILY cause instability.
	 */
	public void setLevelStack(StackGroup newStack) {
		this.levelStack = newStack;
	}
	
	/**
	 * Get a list of all the entities in the map in compilation order.
	 * @return Unmodifiable list of entities.
	 */
	public List<Entity> collapseStack() {
		return levelStack.collapse();
	}

	/**
	 * Get an entity by name.
	 * 
	 * @param name Entity name.
	 * @return The entity, or <code>null</code> if no entity exists by this name.
	 */
	public Entity getEntity(String name) {
		for (Entity ent : levelStack) {
			if (ent.getName().equals(name)) return ent;
		}
		return null;
	}
	
	/**
	 * Get a list of all entities in the level in compile order.
	 */
	public List<Entity> listEntities() {
		List<Entity> list = new ArrayList<>();
		for (Entity ent : levelStack) {
			list.add(ent);
		}
		return list;
	}
	
	/**
	 * Get a list of all entity names in the level in compile order.
	 */
	public List<String> listEntityNames() {
		List<String> list = new ArrayList<>();
		for (Entity ent : levelStack) {
			list.add(ent.getName());
		}
		return list;
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
	 * Get whether this level should compile with a resourcepack.
	 */
	public boolean getEnableResourcepack() {
		return enableResourcepack;
	}

	/**
	 * Set whether this level should compile with a resourcepack.
	 */
	public void setEnableResourcepack(boolean enableResourcepack) {
		this.enableResourcepack = enableResourcepack;
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
	public Function initFunction() {
		return initFunction;
	}
	
	/**
	 * Get this map's init function.
	 * ONLY EXISTS DURING COMPILATION!
	 * @return Tick function.
	 */
	public Function tickFunction() {
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
	
	public void setInitFunction(Function initFunction) {
		this.initFunction = initFunction;
	}
	
	/**
	 * For use in the compiler only.
	 */
	public void setTickFunction(Function tickFunction) {
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
	public String validateName(String name, String... ignore) {
		Set<String> existing = new HashSet<>();
		for (Entity ent : getLevelStack()) {
			existing.add(ent.getName());
		}
		
		return LevelOperations.validateName(name, existing, ignore);
	}
	
	/**
	 * Create a new entitiy.
	 * @param typeName Type name of entity to spawn.
	 * @param name Name of entity.
	 * @param position Position of entity.
	 * @return Newly created entity.
	 */
	public Entity newEntity(String typeName, String name, Vector3f position) {
		// Make sure entity with name doesn't already exist
		name = validateName(name, new String[] {});
		
		Entity entity = EntityRegistry.createEntity(typeName, this, name);
		entity.setAttribute("position", new VectorAttribute(position), true);
		addEntity(entity);
		
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
		addEntity(entity, false);
	}
	
	/**
	 * Add an existing entity object to the level.
	 * <br>
	 * <b>Warning: </b> Only call if you know what you're doing! Can lead to illegal states!
	 * <code>newEntity</code> should be used most of the time.
	 * @param entity Entity to add.
	 * @param stackIndex Index to add it to in the entity stack.
	 * @param noRecompile Don't recompile the level afterward.
	 * @deprecated Entities can no-longer be simply inserted into the stack.
	 */
	public void addEntity(Entity entity, int stackIndex, boolean noRecompile) {
		addEntity(entity, noRecompile);
	}
	
	/**
	 * Add an existing entity object to the level.
	 * <br>
	 * <b>Warning: </b> Only call if you know what you're doing! Can lead to illegal states!
	 * <code>newEntity</code> should be used most of the time.
	 * @param entity Entity to add.
	 * @param noRecompile Don't recompile the level afterward.
	 */
	public void addEntity(Entity entity, boolean noRecompile) {
		addEntity(entity, levelStack, levelStack.items.size(), noRecompile);
	}
	
	/**
	 * Add an existing entity object to the level. <br>
	 * <b>Warning: </b> Only call if you know what you're doing! Can lead to illegal
	 * states! <code>newEntity</code> should be used most of the time.
	 * 
	 * @param entity      Entity to add.
	 * @param group       Stack group to add to.
	 * @param index       Index within group to add at.
	 * @param noRecompile Don't recompile the level afterward.
	 */
	@SuppressWarnings("deprecation")
	public void addEntity(Entity entity, StackGroup group, int index, boolean noRecompile) {
		if (!levelStack.containsGroup(group)) {
			throw new IllegalArgumentException("Entity can only be added to a group within the level!");
		}
		entity.setName(validateName(entity.getName(), new String[] {}));
		
		group.items.add(index, new StackItem(entity));
		updateLevelStack();
		entity.onAdded();
		
		if (entity instanceof BlockEntity) {
			dirtySections.addAll(((BlockEntity) entity).getOverlappingSections());
			if (!noRecompile && autoRecompile) {
				quickRecompile();
			}
		}
	}
	
	/**
	 * Add an existing stack group to the level. <b>Warning: </b> Only call if you
	 * know what you're doing! Can lead to illegal states! An
	 * {@link AddGroupOperation} should be used most of the time.
	 * 
	 * @param group       Group to add.
	 * @param parent      Group to add to.
	 * @param index       Index within group to add at.
	 * @param noRecompile Don't recompile the level afterward.
	 */
	@SuppressWarnings("deprecation")
	public void addGroup(StackGroup group, StackGroup parent, int index, boolean noRecompile) {
		if (!levelStack.containsGroup(parent)) {
			throw new IllegalArgumentException("Parent group is not within the level!");
		}
		boolean recompile = false;
		parent.items.add(index, new StackItem(group));
		for (Entity entity : group) {
			entity.setName(validateName(entity.getName(), new String[] {}));
			if (entity instanceof BlockEntity) {
				dirtySections.addAll(((BlockEntity) entity).getOverlappingSections());
				recompile = true;
			}
			entity.onAdded();
		}
		
		if (!noRecompile && autoRecompile && recompile) {
			quickRecompile();
		}
	}
	
	/**
	 * Remove a stack group from the level.
	 * 
	 * @param group       Group to remove.
	 * @param noRecompile Don't recompile the level afterward.
	 * @return If the group was found in the level.
	 */
	public boolean removeGroup(StackGroup group, boolean noRecompile) {
		if (group.equals(getLevelStack())) {
			throw new IllegalArgumentException("Level stack root cannot be removed!");
		}

		StackGroup parent = levelStack.getOwningGroup(new StackItem(group));
		if (parent == null) return false;
		
		boolean recompile = false;
		parent.items.remove(new StackItem(group));

		for (Entity entity : group) {
			if (entity instanceof BlockEntity) {
				dirtySections.addAll(((BlockEntity) entity).getOverlappingSections());
				recompile = true;
			}
			entity.onRemoved();
		}
		
		if (!noRecompile && autoRecompile && recompile) {
			quickRecompile();
		}
		
		return true;
	}
	
	/**
	 * Remove an entity from the level.
	 * @param entity Entity to remove.
	 */
	public void removeEntity(Entity entity) {
		removeEntity(entity, false);
	}
	
	/**
	 * Remove an entity from the level.
	 * @param entity Entity to remove.
	 * @param noRecompile Don't recompile the level after removal.
	 */
	public void removeEntity(Entity entity, boolean noRecompile) {
		if (entity instanceof BlockEntity) {
			dirtySections.addAll(((BlockEntity) entity).getOverlappingSections());
		}
		
		levelStack.remove(entity);
		updateLevelStack();
		entity.onRemoved();
		
		if (!noRecompile && autoRecompile) {
			quickRecompile();
		}
	}
	
	/**
	 * Rename an entity in the level.
	 * @param target Entity to rename.
	 * @param newName New name.
	 * @param noRefactor Don't refactor references in other entities.
	 * @return Success
	 */
	@SuppressWarnings("deprecation")
	public boolean renameEntity(Entity target, String newName, boolean noRefactor) {
		String oldName = target.getName();
		if (oldName.equals(newName)) return false;
		
		newName = validateName(newName, oldName);
		target.setName(newName);
		
		if (noRefactor) return true;
		refactorEntityName(oldName, newName, false);
		updateLevelStack();
		
		return true;
	}
	
	/**
	 * Refactor an entity name. Does not rename the entity itself. For that, use
	 * {@link #renameEntity(Entity, String, boolean)}.
	 * 
	 * @param oldName       Old entity name.
	 * @param newName       New entity name.
	 * @param supressUpdate Don't call {@link Entity#onUpdateAttributes(boolean)} on
	 *                      entities with their attributes updated.
	 * @return The number of entities that were updated.
	 */
	public int refactorEntityName(String oldName, String newName, boolean supressUpdate) {
		Set<Entity> updated = new HashSet<>();
		for (Entity entity : levelStack) {
			if (entity.refactorName(oldName, newName, true) > 0) {
				updated.add(entity);
				if (!supressUpdate) entity.onUpdateAttributes(true);
			}
		}
		
		if (autoRecompile && updated.size() > 0) quickRecompile();
		return updated.size();
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
				public TargetSelector getTargetSelector() {
					return TargetSelector.fromString("@e[type="+SCOREBOARDTYPE+", name="+SCOREBOARDNAME+"]");
				}
			};
		}
		
		return scoreboardEntity;
	}
	
	
	public Command summonScoreboardEntity() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("CustomName","\""+SCOREBOARDNAME+"\"");
		nbt.putInt("Duration", 2000000000);
		
		try {
			return Command.fromString("summon "+SCOREBOARDTYPE+" 0 0 0 "+SNBTUtil.toSNBT(nbt));
		} catch (IOException e) {
			throw new AssertionError("Unable to spawn scoreboard entity due to an NBT error.", e);
		}
	}

	public void saveFile(File file) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		new LevelWriter(buffer).write(this);
		
		FileOutputStream fileOut = new FileOutputStream(file);
		buffer.writeTo(fileOut);
		fileOut.close();
		setHasUnsavedChanges(false);
		LogManager.getLogger().info("Level saved to " + file);
	}
	
	public void saveFile(String file) throws IOException {
		saveFile(project.assetManager().getAbsoluteFile(file));
	}
	
	/**
	 * Load a level from a file.
	 * @param project Project to load into
	 * @param file File to load
	 * @return Loaded level
	 * @throws IOException If an IO Exception occurs while loading.
	 */
	public static Level loadFile(Project project, File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		Level level = new LevelReader(in).read(project);
		level.setName(FilenameUtils.getBaseName(level.getName()));
		return level;
	}
	
	/**
	 * Load a level from a file.
	 * @param project Project to load into
	 * @param file File to load
	 * @return Loaded level
	 * @throws IOException If an IO Exception occurs while loading.
	 */
	public static Level loadFile(Project project, String file) throws IOException {
		return loadFile(project, project.assetManager().getAbsoluteFile(file));
	}
	
	/**
	 * Compile the entire blockworld (and game entities within the blockworld).
	 * @param full Should this be a full compile? If true, entities may run more complex algorithems.
	 */
	public void compileBlockWorld(boolean full) {		
		blockWorld.clear(); // Clear the blockworld of previous compiles.
		LogManager.getLogger().info("Compiling world...");
		
		for (Entity entity: levelStack) {
			if (entity instanceof BlockEntity) {
				BlockEntity blockEntity = (BlockEntity) entity;
				try {
					blockEntity.compileWorld(blockWorld, full);
				} catch (Throwable e) {
					LOGGER.error("Unable to compile world entity: "+name, e);
				}
			}
			if (entity instanceof EntityProvider) {
				EntityProvider adder = (EntityProvider) entity;
				try {
					adder.compileGameEntities(blockWorld);
				} catch (Throwable e) {
					LOGGER.error("Unable to compile game entities for: "+name, e);
				}
			}
		}
		fireWorldUpdateEvent(new HashSet<>());
		dirtySections.clear();
		LogManager.getLogger().info("Finished compiling world.");
	}
	
	/**
	 * Compile all the game entities in the level.
	 */
	public void compileGameEntities() {
		for (Chunk c : blockWorld.chunks()) {
			c.entities.clear();
		}
		
		for (Entity entity : levelStack) {
			if (entity instanceof EntityProvider) {
				EntityProvider adder = (EntityProvider) entity;
				try {
					adder.compileGameEntities(blockWorld);
				} catch (Throwable e) {
					LOGGER.error("Unable to compile game entities for: "+name, e);
				}
			}
		}
	}
	
	/**
	 * Compile a specific set of sections. Less efficient than
	 * <code>compileBlockWorld()</code> and <code>compileChunks()</code> if
	 * compiling the entire world or an entire set of chunks. DOES NOT COMPILE GAME
	 * ENTITIES!
	 * 
	 * @param sections Sections to compile.
	 */
	public void compileSections(Set<SectionCoordinate> sections) {
		if (sections == null ||sections.isEmpty()) {
			return;
		}
		LogManager.getLogger().info("Compiling sections...");
		// Compile into a temporary block world so other chunks don't get corrupted.
		BlockWorld tempWorld = new BlockWorld();
		
		List<BlockEntity> updatingEntities = new ArrayList<>();
		
		for (Entity entity : levelStack) {
			if (entity instanceof BlockEntity) {
				BlockEntity blockEntity = (BlockEntity) entity;
				Vector3i[] bounds = blockEntity.getBounds();
				Vector3i minSection = bounds[0].toFloat().divide(16).floor();
				Vector3i maxSection = bounds[1].toFloat().divide(16).floor();
				// See if entity is within chunkList.
				for (SectionCoordinate section : sections) {
					if (minSection.x <= section.x && section.x <= maxSection.x
							&& minSection.y <= section.y && section.y <= maxSection.y
							&& minSection.z <= section.z && section.z <= maxSection.z) {
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
			try {
				entity.compileWorld(tempWorld, false, sections);
			} catch (Throwable e) {
				LOGGER.error("Unable to compile world entity: "+entity, e);
			}
		}
		
		for (SectionCoordinate coord : sections) {
			Chunk chunk = blockWorld.chunkAt(coord.x, coord.z);
			if (chunk == null) {
				chunk = new Chunk();
				blockWorld.getChunks().put(coord.getChunk(), chunk);
			}
			Chunk tempChunk = tempWorld.chunkAt(coord.x, coord.z);
			if (tempChunk == null) {
				tempChunk = new Chunk();
			}

			chunk.sections[coord.y] = tempChunk.sections[coord.y];
		}
		LogManager.getLogger().info("Finished compiling world.");
		return;
	}
	
	/**
	 * Compile all the chunks marked as dirty. <br>
	 * Note: {@link #autoRecompile} should be checked before calling unless you
	 * explicitly want to bypass it.
	 */
	public void quickRecompile() {
		if (dirtySections.isEmpty()) return;
		
		compileSections(dirtySections);
//		compileBlockWorld(false);
		fireWorldUpdateEvent(dirtySections);
		dirtySections.clear();
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
	public void updateLevelStack() {
		for (Runnable listener : updateStackListeners) {
			listener.run();
		}
	}
	
	/**
	 * Register a listener to be called when one {@link RenderEntity} or more have
	 * updated. If a render entity that previously existed isn't present in the
	 * list, it should be removed.
	 * 
	 * @param listener Event listener.
	 */
	public void onUpdateRenderEntities(EventListener<UpdateRenderEntitiesEvent> listener) {
		updateRenderEntitiesDispatcher.addListener(listener);
	}
	
	public void fireUpdateRenderEntitiesEvent(UpdateRenderEntitiesEvent event) {
		updateRenderEntitiesDispatcher.fire(event);
	}
	
	/**
	 * Manually update the visual representations of all the entities in the level.
	 */
	public void updateRenderEntities() {
		for (Entity entity : getEntities().values()) {
			entity.updateRenderEntities();
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
	
	
	// UUID CACHE
	
	private Map<Entity, UUID> uuidCache = new HashMap<>();
	
	/**
	 * Get the UUID of a potential MC companion entity to a Scaffold entitiy.
	 * Whether or not this entity exists is up to the implementation of the Scaffold
	 * entity. Note: these UUIDs are non-persistant and are only guarenteed to
	 * remain consistant throughout compilation. They may not be serialized.
	 * 
	 * @param entity Entity to get the UUID of.
	 * @return Companion UUID.
	 */
	public UUID getCompanionUUID(Entity entity) {
		UUID val = uuidCache.get(entity);
		if (val == null) {
			val = UUID.randomUUID();
			uuidCache.put(entity, val);
		}
		return val;
	}
		
}
