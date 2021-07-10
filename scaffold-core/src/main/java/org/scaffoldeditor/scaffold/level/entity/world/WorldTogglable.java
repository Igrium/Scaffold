package org.scaffoldeditor.scaffold.level.entity.world;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.nbt.schematic.GenericSchematic;
import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.LogicUtils;
import org.scaffoldeditor.scaffold.logic.datapack.Function;
import org.scaffoldeditor.scaffold.logic.datapack.StructureFunction;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3f;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.DataCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.Conditional;
import org.scaffoldeditor.scaffold.sdoc.SDoc;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.commands.FunctionCommand;

import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;


public class WorldTogglable extends WorldStatic {
	
	public static void register() {
		EntityRegistry.registry.put("world_togglable", new EntityFactory<WorldTogglable>() {

			@Override
			public WorldTogglable create(Level level, String name) {
				return new WorldTogglable(level, name);
			}
		});
	}

	public WorldTogglable(Level level, String name) {
		super(level, name);	
	}
	
	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = super.getDefaultAttributes();
		map.put("start_enabled", new BooleanAttribute(true));
		return map;
	}
	
	public boolean startEnabled() {
		return (Boolean) getAttribute("start_enabled").getValue();
	}
	
	private GenericSchematic underlayerCache;
	
	@Override
	public boolean compileWorld(BlockWorld world, boolean full, Set<SectionCoordinate> sections) {
		// Save the underlayer.
		if (full) {
			reload();
			SizedBlockCollection model = getFinalModel();
			
			if (model != null) {
				Vector3i offset = model.getMin();
				Vector3i max = model.getMax();
				
				underlayerCache = new GenericSchematic(max.x - offset.x, max.y - offset.y, max.z - offset.z);
				for (Vector3i block : model) {
					if (shouldPlaceAir() || !model.blockAt(block).getName().equals("minecraft:air")) {
						Block worldBlock = world.blockAt(getBlockPosition().add(block));
						if (worldBlock == null) worldBlock = new Block("minecraft:air");
						underlayerCache.setBlock(worldBlock, block.add(offset));
					}
				}
			}
		}
		
		if (startEnabled()) {
			return super.compileWorld(world, full, sections);
		} else {
			return true;
		}
	}
	
	@Override
	public Collection<InputDeclaration> getDeclaredInputs() {
		Collection<InputDeclaration> in = super.getDeclaredInputs();
		in.add(new InputDeclaration() {
			
			@Override
			public String getName() {
				return "toggle";
			}
		});
		in.add(new InputDeclaration() {
			
			@Override
			public String getName() {
				return "enable";
			}
		});
		in.add(new InputDeclaration() {
			
			@Override
			public String getName() {
				return "disable";
			}
		});
		return in;
	}
	
	@Override
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source, Entity instigator) {
		if (inputName.equals("toggle")) {
			return List.of(getLocalExecute(new FunctionCommand(getToggleFunction())));
		}
		if (inputName.equals("enable")) {
			return List.of(getLocalExecute(new FunctionCommand(getEnableFunction())));
		}
		if (inputName.equals("disable")) {
			return List.of(getLocalExecute(new FunctionCommand(getDisableFunction())));
		}
		
		return super.compileInput(inputName, args, source, instigator);
	}
	
	private Command getLocalExecute(Command run) {
		return new ExecuteCommandBuilder().positioned(new CommandVector3f(getPosition())).run(run);
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		
		if (getFinalModel() == null) return super.compileLogic(datapack);
		StructureFunction enableImpl = new StructureFunction(new Identifier(getLevel().getName().toLowerCase(), getName().toLowerCase()+"/enable_impl"), getFinalModel());
		enableImpl.placeAir = shouldPlaceAir();
		datapack.functions.add(enableImpl);
		
		StructureFunction disableImpl = new StructureFunction(getLevel().getName().toLowerCase(), getName().toLowerCase()+"/disable_impl", underlayerCache);
		disableImpl.placeAir = true;
		datapack.functions.add(disableImpl);
		
		Function enableFunction = new Function(getEnableFunction());
		enableFunction.commands.add(new FunctionCommand(enableImpl));
		enableFunction.commands.add(setEnabledCommand(true));
		datapack.functions.add(enableFunction);
		
		Function disableFunction = new Function(getDisableFunction());
		disableFunction.commands.add(new FunctionCommand(disableImpl));
		disableFunction.commands.add(setEnabledCommand(false));
		datapack.functions.add(disableFunction);
		
		Identifier storage = LogicUtils.getEntityStorage(this);
		Function toggleFunction = new Function(getToggleFunction());
		toggleFunction.commands.add(LogicUtils.cloneNBT(storage, "enabled", storage, "wasEnabled"));
		
		toggleFunction.addExecuteBlock(new ExecuteCommandBuilder().executeIf(wasEnabled()), Arrays.asList(
					new FunctionCommand(disableImpl),
					new DataCommandBuilder().modify().storage(storage).path("enabled").set().value(new ByteTag(false)).build()
				));
		
		toggleFunction.addExecuteBlock(new ExecuteCommandBuilder().executeUnless(wasEnabled()), Arrays.asList(
					new FunctionCommand(enableImpl),
					new DataCommandBuilder().modify().storage(storage).path("enabled").set().value(new ByteTag(true)).build()
				));
		
		datapack.functions.add(toggleFunction);
		
		CompoundTag defaultStorage = new CompoundTag();
		defaultStorage.putBoolean("enabled", startEnabled());
		datapack.defaultStorage.put(LogicUtils.getEntityStorage(this), defaultStorage);
		
		return super.compileLogic(datapack);
	};
	
	public Conditional isEnabled() {
		return LogicUtils.ifStorageHas(this, "{enabled:1b}");
	}
	
	private Conditional wasEnabled() {
		return LogicUtils.ifStorageHas(this, "{wasEnabled:1b}");
	}
	
	public Identifier getEnableFunction() {
		return new Identifier(getLevel().getName().toLowerCase(), getName().toLowerCase()+"/enable");
	}
	
	public Identifier getDisableFunction() {
		return new Identifier(getLevel().getName().toLowerCase(), getName().toLowerCase()+"/disable");
	}
	
	public Identifier getToggleFunction() {
		return new Identifier(getLevel().getName().toLowerCase(), getName().toLowerCase()+"/toggle");
	}
	
	private Command setEnabledCommand(boolean enabled) {
		CompoundTag storage = new CompoundTag();
		storage.putBoolean("enabled", enabled);
		return new DataCommandBuilder().merge().storage(LogicUtils.getEntityStorage(this)).nbt(storage).build();
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/world_togglable.sdoc");
	}
}
