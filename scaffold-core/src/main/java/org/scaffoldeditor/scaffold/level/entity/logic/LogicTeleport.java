package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EntityAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3f;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;

/***
 * Teleports Minecraft entities to this entity's location.
 * @author Igrium
 *
 */
public class LogicTeleport extends LogicEntity {
	
	public static void register() {
		EntityRegistry.registry.put("logic_teleport", LogicTeleport::new);
	}

	public LogicTeleport(Level level, String name) {
		super(level, name);
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/teleport.png";
	}

	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = new HashMap<>();
		map.put("target", new StringAttribute("@s"));
		map.put("relative", new BooleanAttribute(false));
		map.put("landmark", new EntityAttribute(""));
		return map;
	}
	
	@Override
	public Collection<InputDeclaration> getDeclaredInputs() {
		Collection<InputDeclaration> inputs = super.getDeclaredInputs();
		inputs.add(new InputDeclaration() {
			@Override
			public String getName() {
				return "teleport";
			}
		});
		return inputs;
	}
	
	@Override
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source, Entity instigator) {
		if (inputName.equals("teleport")) {
			return List.of(getCommand());
		}
		
		return super.compileInput(inputName, args, source, instigator);
	}
	
	/**
	 * Generate the teleport command.
	 * @return Teleport command.
	 */
	public Command getCommand() {
		if (isRelative() && getLandmark() != null) {
			Entity landmark = getLandmark();
			Vector3f relative = getPosition().subtract(landmark.getPosition());
			Command relativeCommand = Command.fromString("teleport @s "+new CommandVector3f(relative, CommandVector.Mode.RELATIVE));
			return new ExecuteCommandBuilder().as(TargetSelector.fromString(getTarget())).at(TargetSelector.SELF).run(relativeCommand);
			
		} else {
			return Command.fromString("teleport "+getTarget()+" "+new CommandVector3f(getPosition()));
		}		
	}
	
	public String getTarget() {
		return (String) getAttribute("target").getValue();
	}
	
	public boolean isRelative() {
		return (Boolean) getAttribute("relative").getValue();
	}
	
	public Entity getLandmark() {
		return getLevel().getEntity((String) getAttribute("landmark").getValue());
	}

}
