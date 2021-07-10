package org.scaffoldeditor.scaffold.level.entity.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.IntAttribute;
import org.scaffoldeditor.scaffold.level.entity.world.BaseSingleBlock;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.LogicUtils;
import org.scaffoldeditor.scaffold.logic.datapack.Function;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.BlockArguement;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3i;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ScheduleCommand;
import org.scaffoldeditor.scaffold.logic.datapack.commands.SetBlockCommand;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

/**
 * Fires a redstone output when it recieves an input.
 * @author Igrium
 */
public class RedstoneTrigger extends BaseSingleBlock {
	
	public static void register() {
		EntityRegistry.registry.put("redstone_trigger", RedstoneTrigger::new);
	}
	
	public static final Block ENABLED_BLOCK = new Block("minecraft:redstone_block");
	public static final Block DISABLED_BLOCK = new Block("minecraft:lapis_block");
	public RedstoneTrigger(Level level, String name) {
		super(level, name);
	}
	
	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = new HashMap<>();
		map.put("start_enabled", new BooleanAttribute(false));
		map.put("pulse_length", new IntAttribute(20));
		return map;
	}
	
	@Override
	public Collection<InputDeclaration> getDeclaredInputs() {
		Collection<InputDeclaration> in = super.getDeclaredInputs();
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
		
		in.add(new InputDeclaration() {
			@Override
			public String getName() {
				return "pulse";
			}

		});
		
		return in;
	}
	
	public int getPulseLength() {
		return ((IntAttribute) getAttribute("pulse_length")).getValue();
	}
	
	@Override
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source, Entity instigator) {
		if (inputName.equals("enable")) {
			return List.of(enableCommand());
		}
		if (inputName.equals("disable")) {
			return List.of(disableCommand());
		}
		if (inputName.equals("pulse")) {
			List<Command> list = new ArrayList<>();
			list.add(enableCommand());
			list.add(new ScheduleCommand(disableFunction(), getPulseLength()));
			return list;
		}
		
		return super.compileInput(inputName, args, source, instigator);
	}
	
	public boolean startEnabled() {
		return (Boolean) getAttribute("start_enabled").getValue();
	}
	
	public Command enableCommand() {
		return new SetBlockCommand(new CommandVector3i(getBlockPosition()), new BlockArguement(ENABLED_BLOCK),
				SetBlockCommand.Mode.REPLACE);
	}
	
	public Command disableCommand() {
		return new SetBlockCommand(new CommandVector3i(getBlockPosition()), new BlockArguement(DISABLED_BLOCK),
				SetBlockCommand.Mode.REPLACE);
	}
	
	private Identifier disableFunction() {
		return LogicUtils.getEntityFunction(this, "disable");
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		Function disable = new Function(disableFunction());
		disable.commands.add(disableCommand());
		datapack.functions.add(disable);
		
		return super.compileLogic(datapack);
	}

	@Override
	public Block getBlock() {
		return startEnabled() ? ENABLED_BLOCK : DISABLED_BLOCK;
	}

	@Override
	protected boolean needsRecompiling() {
		return true;
	}

	@Override
	public void onUpdateBlockAttributes() {
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/redstone_trigger.sdoc");
	}
}
