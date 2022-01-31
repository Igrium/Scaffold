package org.scaffoldeditor.scaffold.entity.path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.EntityProvider;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.entity.attribute.EntityAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.FloatAttribute;
import org.scaffoldeditor.scaffold.entity.game.KnownUUID;
import org.scaffoldeditor.scaffold.entity.logic.LogicEntity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.LogicUtils;
import org.scaffoldeditor.scaffold.logic.datapack.Function;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.TemplateFunction;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.commands.FunctionCommand;
import org.scaffoldeditor.scaffold.sdoc.SDoc;
import org.scaffoldeditor.scaffold.util.UUIDUtils;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.ListTag;

public class PathTrain extends LogicEntity implements KnownUUID, EntityProvider {
	
	public static void register() {
		EntityRegistry.registry.put("path_train", PathTrain::new);
	}
	
	protected static final String TICK_FUNCTION_TEMPLATE = "functions/scaffold/train_tick.mcfunction";
	protected static final String JUMP_FUNCTION_TEMPLATE = "functions/scaffold/train_jump.mcfunction";
	protected static final String TRY_JUMP_TEMPLATE = "functions/scaffold/train_try_jump.mcfunction";

	public PathTrain(Level level, String name) {
		super(level, name);	
	}

	@Attrib(name = "starting_path_node")
	protected EntityAttribute startingPathNode = new EntityAttribute("");

	@Attrib
	protected FloatAttribute speed = new FloatAttribute(1f);
	
	@Override
	public Collection<OutputDeclaration> getDeclaredOutputs() {
		Collection<OutputDeclaration> outs = super.getDeclaredOutputs();
		outs.add(() -> "on_passed_node");
		return outs;
	}
	
	@Override
	public Collection<InputDeclaration> getDeclaredInputs() {
		Collection<InputDeclaration> in = super.getDeclaredInputs();
		in.add(() -> "start_forward");
		in.add(() -> "stop");
		in.add(() -> "reset");
		return in;
	}
	
	@Override
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source, Entity instigator) {
		if (inputName.equals("start_forward")) {
			return List.of(Command.fromString("tag "+getTargetSelector().compile()+" add forward"));
		}
		if (inputName.equals("stop")) {
			return List.of(Command.fromString("tag "+getTargetSelector().compile()+" remove forward"));
		}
		if (inputName.equals("reset")) {
			List<Command> commands = new ArrayList<>();
			commands.addAll(compileInput("stop", args, source, instigator));
			PathNode start = getStartingPath();
			if (start != null) {
				commands.add(jumpToNode(getStartingPath()));
			}
			return commands;
		}
		
		return super.compileInput(inputName, args, source, instigator);
	}
	
	/**
	 * Obtain a command that will teleport this train to a specific path node.
	 * @param node Node to jump to.
	 * @return Generated command.
	 */
	public Command jumpToNode(PathNode node) {
		return new ExecuteCommandBuilder().as(node.getTargetSelector()).at(TargetSelector.SELF)
				.run(new FunctionCommand(jumpHereFunction()));
	}

	
	/**
	 * Get how many blocks the train should move each tick;
	 */
	public float getTeleDistance() {
		float speed = (float) getAttribute("speed").getValue();
		return speed / 20f;
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		TemplateFunction jumpFunction;
		try {
			jumpFunction = TemplateFunction.fromAsset(getProject().assetManager(), jumpHereFunction(), JUMP_FUNCTION_TEMPLATE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		jumpFunction.setVariable("this", getTargetSelector().compile());
		
		// Runs in the scope of the path node.
		Function jump = new Function(jumpFunction.getID());
		jump.commands.addAll(jumpFunction.getCommands());
		jump.addExecuteBlock(new ExecuteCommandBuilder().at(TargetSelector.SELF), compileOutput("on_passed_node"));
		
		
		for (PathNode node : getStartingPath().getPath()) {
			ExecuteCommandBuilder builder = new ExecuteCommandBuilder().executeIf(LogicUtils.hasUUID(node.getUUID())).at(TargetSelector.SELF);
			jump.addExecuteBlock(builder, node.compileOutput(PathNode.PASSED_OUTPUT, this));
		}
		
		datapack.functions.add(jump);
		
		TemplateFunction tryJump;
		try {
			tryJump = TemplateFunction.fromAsset(getProject().assetManager(),
					LogicUtils.getEntityFunction(this, "try_jump"), TRY_JUMP_TEMPLATE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		tryJump.setVariable("this", getTargetSelector().compile());
		tryJump.setVariable("jump", jumpFunction.getID().toString());
		datapack.functions.add(tryJump);
		
		TemplateFunction tick;
		try {
			tick = TemplateFunction.fromAsset(getProject().assetManager(),
					LogicUtils.getEntityFunction(this, "tick"), TICK_FUNCTION_TEMPLATE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		tick.setVariable("speed", Float.toString(getTeleDistance()));
		tick.setVariable("try_jump", tryJump.getID().toString());
		datapack.functions.add(tick);
		
		getLevel().tickFunction().commands.add(new ExecuteCommandBuilder().as(getTargetSelector())
				.at(TargetSelector.SELF).run(new FunctionCommand(tick)));
				
		return super.compileLogic(datapack);
	}
	
	public PathNode getStartingPath() {
		Entity ent = getLevel().getEntity(((EntityAttribute) getAttribute("starting_path_node")).evaluate(this));
		if (ent instanceof PathNode) {
			return (PathNode) ent;
		} else {
			return null;
		}
	}
	
	public CompoundTag getGameEntity() {
		CompoundTag ent = LogicUtils.getCompanionEntity(this);
		CompoundTag data = ent.getCompoundTag("data");
		
		PathNode startingPath = getStartingPath();
		if (startingPath != null) {
			data.putIntArray("CurrentPath", UUIDUtils.toIntArray(startingPath.getUUID()));
			PathNode next = startingPath.getNext();
			if (next != null) {
				data.putIntArray("NextPath", UUIDUtils.toIntArray(next.getUUID()));
			}
		}
		
		return ent;
	}
	
	/**
	 * Obtain the name of a function that, when called in the scope of a path node,
	 * will jump this train to that node.
	 * 
	 * @return Jump here function.
	 */
	public Identifier jumpHereFunction() {
		return LogicUtils.getEntityFunction(this, "jump_here");
	}

	@Override
	public UUID getUUID() {
		return getLevel().getCompanionUUID(this);
	}

	@Override
	public boolean compileGameEntities(BlockWorld world) {
		CompoundTag ent = getGameEntity();
		PathNode startingPath = getStartingPath();
		if (startingPath != null) {
			double[] rot = startingPath.getRotation();
			ListTag<FloatTag> rotation = new ListTag<>(FloatTag.class);
			rotation.addFloat((float) rot[0]);
			rotation.addFloat((float) rot[1]);
			ent.put("Rotation", rotation);
			
			world.addEntity(ent, startingPath.getPosition());
		} else {
			world.addEntity(ent, getPosition());
		}
		
		return true;
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/path_train.sdoc", super.getDocumentation());
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/minecart.png";
	}
	
	@Override
	public boolean isGridLocked() {
		return false;
	}
}
