package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Collection;
import java.util.List;

import org.joml.Vector3d;
import org.scaffoldeditor.scaffold.annotation.Attrib;
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
import org.scaffoldeditor.scaffold.sdoc.SDoc;

/***
 * Teleports Minecraft entities to this entity's location.
 * @author Igrium
 *
 */
public class LogicTeleport extends LogicEntity {
	
	public static void register() {
		EntityRegistry.registry.put("logic_teleport", LogicTeleport::new);
	}

	@Attrib
	StringAttribute target = new StringAttribute("@s");
	
	@Attrib
	BooleanAttribute relative = new BooleanAttribute(false);

	@Attrib
	EntityAttribute landmark = new EntityAttribute("");

	public LogicTeleport(Level level, String name) {
		super(level, name);
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/teleport.png";
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
			Vector3d relative = new Vector3d(getPosition()).sub(landmark.getPosition());
			Command relativeCommand = Command.fromString("teleport @s "+new CommandVector3f(relative, CommandVector.Mode.RELATIVE));
			return new ExecuteCommandBuilder().as(TargetSelector.fromString(getTarget())).at(TargetSelector.SELF).run(relativeCommand);
		} else {
			return Command.fromString("teleport "+getTarget()+" "+new CommandVector3f(getPosition()));
		}		
	}
	
	public String getTarget() {
		return target.getValue();
	}
	
	public boolean isRelative() {
		return relative.getValue();
	}
	
	public Entity getLandmark() {
		return getLevel().getEntity(landmark.getValue());
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/logic_teleport.sdoc", super.getDocumentation());
	}
}
