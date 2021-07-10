package org.scaffoldeditor.scaffold.level.entity.world;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockCollection;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.nbt.util.MCEntity;
import org.scaffoldeditor.scaffold.io.AssetLoader;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityProvider;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.AssetAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.game.KnownUUID;
import org.scaffoldeditor.scaffold.level.entity.game.TargetSelectable;
import org.scaffoldeditor.scaffold.level.render.ModelRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.LogicUtils;
import org.scaffoldeditor.scaffold.logic.datapack.Function;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3f;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector.Mode;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.DataCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.Conditional;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.DataEntityConditional;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.constraints.ParentConstraint;
import org.scaffoldeditor.scaffold.sdoc.SDoc;
import org.scaffoldeditor.scaffold.util.UUIDUtils;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;

/**
 * Represents a collection of blocks that's rendered as a set of entities,
 * allowing them to be moved around off-grid. Don't use for big block
 * collections because it's very expensive. Each block gets its own
 * <code>falling_block</code> entity that renders it.
 * 
 * @author Igrium
 *
 */
public class WorldDynamic extends Entity implements EntityProvider, TargetSelectable {
	
	public static void register() {
		EntityRegistry.registry.put("world_dynamic", new EntityFactory<WorldDynamic>() {

			@Override
			public WorldDynamic create(Level level, String name) {
				return new WorldDynamic(level, name);
			}
		});
	}

	public WorldDynamic(Level level, String name) {
		super(level, name);
	}
	
	// Expected to remain persistant throughout compilation process.
	private Map<Vector3i, MCEntity> entities;
	private BlockCollection model;
	private String modelPath = "";

	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = new HashMap<>();
		map.put("model", new AssetAttribute("schematic", ""));
		map.put("start_enabled", new BooleanAttribute(true));
		return map;
	}
	
	@Override
	public void onAdded() {
		super.onAdded();
		reloadModel();
	}
	
	private void reloadModel() {
		modelPath = (String) getAttribute("model").getValue();
		
		if (modelPath.length() == 0) {
			model = null;
			return;
		}
		
		AssetLoader<?> loader = getProject().assetManager().getLoader(modelPath);
		if (loader == null || !loader.isAssignableTo(BlockCollection.class)) {
			throw new IllegalStateException(modelPath + " cannot be loaded as a schematic!");
		}
		
		try {
			model = (BlockCollection) getProject().assetManager().loadAsset(modelPath, false);
		} catch (IOException e) {
			LogManager.getLogger().error("Unable to load model: "+modelPath, e);
			return;
		}
		
		entities = new HashMap<>();
		
		for (Vector3i coord : model) {
			if (model.blockAt(coord).getName().equals("minecraft:air")) continue;
			entities.put(coord, generateEntity(model.blockAt(coord)));
		}
	}
	
	@Override
	public void onUpdateAttributes(boolean noRecompile) {
		super.onUpdateAttributes(noRecompile);
		if (!modelPath.equals(getAttribute("model").getValue())) reloadModel();
	}
	
	@Override
	public Set<RenderEntity> getRenderEntities() {
		Set<RenderEntity> renderEnts = super.getRenderEntities();
		if (model == null) return renderEnts;
		
		for (Vector3i pos : entities.keySet()) {
			renderEnts.add(new ModelRenderEntity(this, getPreviewPosition().add(pos.toFloat()), new Vector3f(0, 0, 0),
					"ent" + pos.toString(),
					entities.get(pos).getNBT().getCompoundTag("BlockState").getString("Name") + "#Inventory"));
		}
		
		return renderEnts;
	}
	
	private MCEntity generateEntity(Block block) {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("id", "minecraft:falling_block");
		nbt.putBoolean("NoGravity", true);
		nbt.putBoolean("Invulnerable", true);
		nbt.putInt("Time", 1);
		nbt.putIntArray("UUID", UUIDUtils.toIntArray(UUID.randomUUID()));
		CompoundTag blockState = new CompoundTag();
		blockState.putString("Name", block.getName());
		blockState.put("Properties", block.getProperties());
		nbt.put("BlockState", blockState);
		return new MCEntity("minecraft:falling_block", nbt);
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {		
		
		if (model == null) return super.compileLogic(datapack);
		
		Function tickFunction = new Function(LogicUtils.getEntityFunction(this, "tick"));	
		for (Vector3i coord : entities.keySet()) {
			TargetSelectable ent = KnownUUID.fromEntity(entities.get(coord).getNBT());
			ParentConstraint constraint = new ParentConstraint(TargetSelectable.wrap(getRoot()), ent, coord.toFloat());
			tickFunction.commands.addAll(constraint.getCommands());
			try {
				tickFunction.commands.add(new DataCommandBuilder().merge().entity(ent.getTargetSelector()).nbt((CompoundTag) SNBTUtil.fromSNBT("{Time:1}")).build());
			} catch (IOException e) {
				throw new AssertionError(e);
			}
		}
		
		datapack.functions.add(tickFunction);
		datapack.tickFunctions.add(tickFunction.getID());
		
		Function enableFunction = new Function(enableFunction());
		for (Vector3i coord : entities.keySet()) {
			MCEntity ent = entities.get(coord);
			try {
				enableFunction.addExecuteBlock(new ExecuteCommandBuilder().at(getRoot()).executeUnless(isEnabled()), Arrays.asList(new Command[] {
						Command.fromString("summon minecraft:falling_block "
								+ new CommandVector3f(coord.toFloat().subtract(new Vector3f(.5f, 0f, .5f)), Mode.LOCAL)
								+ " " + SNBTUtil.toSNBT(ent.getNBT()))				}));
			} catch (IOException e) {
				throw new AssertionError("Error writing falling block NBT!", e);
			}
		}
		try {
			enableFunction.commands.add(new DataCommandBuilder().merge().entity(getRoot()).nbt((CompoundTag) SNBTUtil.fromSNBT("{enabled:1b}")).build());
		} catch (IOException e) {
			throw new AssertionError();
		}
		datapack.functions.add(enableFunction);
		
		Function disableFunction = new Function(disableFunction());
		for (MCEntity ent : entities.values()) {
			disableFunction.commands.add(Command.fromString("kill "+KnownUUID.fromEntity(ent.getNBT()).getTargetSelector().compile()));
		}
		try {
			disableFunction.commands.add(new DataCommandBuilder().merge().entity(getRoot()).nbt((CompoundTag) SNBTUtil.fromSNBT("{enabled:0b}")).build());
		} catch (IOException e) {
			throw new AssertionError();
		}
		datapack.functions.add(disableFunction);
		
		return super.compileLogic(datapack);
	}
	
	@Override
	public boolean compileGameEntities(BlockWorld world) {
		reloadModel();
		if (model == null) return true;
		
		if (startEnabled()) {
			for (Vector3i coord : entities.keySet()) {
				world.addEntity(entities.get(coord).getNBT(), coord.toFloat().add(getPosition()).subtract(new Vector3f(.5f, 0f, .5f)).toDouble());
			}
		}
		CompoundTag ent = LogicUtils.getCompanionEntity(this);
		ent.getCompoundTag("data").putBoolean("enabled", startEnabled());
		world.addEntity(ent, getPosition().toDouble());
		
		return true;
	}
	
	public Identifier enableFunction() {
		return LogicUtils.getEntityFunction(this, "enable");
	}
	
	public Identifier disableFunction() {
		return LogicUtils.getEntityFunction(this, "disable");
	}
	
	public TargetSelector getRoot() {
		return LogicUtils.getCompanionSelector(this);
	}
	
	public Conditional isEnabled() {
		return new DataEntityConditional(getRoot(), "{enabled:1b}");
	}
	
	public boolean startEnabled() {
		return (Boolean) getAttribute("start_enabled").getValue();
	}

	@Override
	public TargetSelector getTargetSelector() {
		return getRoot();
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getProject().assetManager(), "doc/world_dynamic.sdoc", super.getDocumentation());
	}
}
