package org.scaffoldeditor.scaffold.level.entity.game;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.world.BaseSingleBlock;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.datapack.Function;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.FunctionCommand;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

import net.querz.nbt.tag.CompoundTag;

/**
 * Places a command block that fires an output when it recieves a redstone signal.
 * @author Igrium
 */
public class RedstoneListener extends BaseSingleBlock {
	
	public static void register() {
		EntityRegistry.registry.put("redstone_listener", new EntityFactory<RedstoneListener>() {

			@Override
			public RedstoneListener create(Level level, String name) {
				return new RedstoneListener(level, name);
			}
		});
	}

	public RedstoneListener(Level level, String name) {
		super(level, name);
	}
		
	private List<Command> commandCache;
	
	@Override
	public Collection<OutputDeclaration> getDeclaredOutputs() {
		Collection<OutputDeclaration> out = super.getDeclaredOutputs();
		out.add(new OutputDeclaration() {
			
			@Override
			public String getName() {
				return "on_powered";
			}
			
			@Override
			public List<String> getArguements() {
				return Collections.emptyList();
			}
		});
		return out;
	}

	@Override
	public boolean compileWorld(BlockWorld world, boolean full, Set<SectionCoordinate> sections) {
		super.compileWorld(world, full, sections);
		
		commandCache = compileOutput("on_powered");
		String command;
		if (commandCache.isEmpty()) {
			command = "";
		} else if (commandCache.size() == 1) {
			command = commandCache.get(0).compile();
		} else {
			command = new FunctionCommand(getTriggerFunction()).compile();
		}
				
		CompoundTag entity = new CompoundTag();
		entity.putBoolean("auto", false);
		entity.putString("Command", command);
		entity.putString("id", "minecraft:command_block");
		entity.putBoolean("keepPacked", false);
		entity.putBoolean("powered", false);
		entity.putInt("SuccessCount", 0);
		entity.putBoolean("TrackOutput", false);
		entity.putBoolean("UpdateLastExecution", true);
		entity.putBoolean("conditionMet", true);
		entity.putString("LastOutput", "");
		
		world.addBlockEntity(getBlockPosition(), entity);
		
		return true;
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		if (commandCache != null && commandCache.size() > 1) {
			Function triggerFunction = new Function(getTriggerFunction());
			triggerFunction.commands.addAll(commandCache);
			datapack.functions.add(triggerFunction);
		}
		
		commandCache = null;
		return super.compileLogic(datapack);
	}
	
	public Block getBlock() {
		String name = isRepeating() ? "minecraft:repeating_command_block" : "minecraft:command_block";
		return new Block(name);
	}

	@Override
	protected boolean needsRecompiling() {
		return true;
	}

	@Override
	public void onUpdateBlockAttributes() {
	}

	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map <String, Attribute<?>> def = new HashMap<>();
		def.put("repeating", new BooleanAttribute(false));
		return def;
	}
	
	public boolean isRepeating() {
		return (Boolean) getAttribute("repeating").getValue();
	}
	
	protected Identifier getTriggerFunction() {
		return new Identifier(getLevel().getName().toLowerCase(), getName().toLowerCase()+"/trigger");
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/redstone_listener.sdoc", super.getDocumentation());
	}
}
