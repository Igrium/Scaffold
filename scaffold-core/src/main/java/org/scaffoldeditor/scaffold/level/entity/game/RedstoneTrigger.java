package org.scaffoldeditor.scaffold.level.entity.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.world.BaseSingleBlock;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.BlockArguement;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3i;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.SetBlockCommand;

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
			
			@Override
			public List<String> getArguements() {
				return Arrays.asList(new String[] { "int_attribute" });
			}
		});
		
		return in;
	}
	
	@Override
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source) {
		if (inputName.equals("enable")) {
			return List.of(enableCommand());
		}
		if (inputName.equals("disable")) {
			return List.of(disableCommand());
		}
		
		return super.compileInput(inputName, args, source);
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

}
