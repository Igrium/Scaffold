package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.IntAttribute;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.LogicUtils;
import org.scaffoldeditor.scaffold.logic.datapack.Function;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.DataCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.Conditional;

import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;

import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.commands.FunctionCommand;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ScheduleCommand;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ScheduleCommand.Mode;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

public class LogicTimer extends LogicEntity {
	
	public static void register() {
		EntityRegistry.registry.put("logic_timer", LogicTimer::new);
	}

	public LogicTimer(Level level, String name) {
		super(level, name);
	}
	
	@Attrib
	IntAttribute time = new IntAttribute(20);

	@Attrib(name = "start_active")
	BooleanAttribute startActive = new BooleanAttribute(true);

	@Attrib(name = "fire_on_activate")
	BooleanAttribute fireOnActivate = new BooleanAttribute(false);
	
	public int getTime() {
		return time.getValue();
	}
	
	public boolean startActive() {
		return startActive.getValue();
	}
	
	public boolean fireOnActivate() {
		return fireOnActivate.getValue();
	}
	
	@Override
	public Collection<OutputDeclaration> getDeclaredOutputs() {
		Collection<OutputDeclaration> outs = super.getDeclaredOutputs();
		outs.add(() -> "on_fire");
		return outs;
	}
	
	@Override
	public Collection<InputDeclaration> getDeclaredInputs() {
		Collection<InputDeclaration> ins = super.getDeclaredInputs();
		ins.add(() -> "enable");
		ins.add(() -> "disable");
		ins.add(() -> "reset");
		return ins;
	}
	
	@Override
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source, Entity instigator) {
		if (inputName.equals("enable")) {
			List<Command> commands = new ArrayList<>();
			commands.add(new DataCommandBuilder().modify().storage(LogicUtils.getEntityStorage(this)).path("enabled")
					.set().value(new ByteTag(true)).build());
			if (fireOnActivate()) {
				commands.add(new FunctionCommand(getFireFunction()));
			} else {
				commands.add(getScheduleCommand());
			}
			return commands;
		}
		
		if (inputName.equals("disable")) {
			List<Command> commands = new ArrayList<>();
			commands.add(new DataCommandBuilder().modify().storage(LogicUtils.getEntityStorage(this)).path("enabled")
					.set().value(new ByteTag(false)).build());
			commands.add(Command.fromString("schedule clear "+getFireFunction()));
			return commands;
		}
		
		if (inputName.equals("reset")) {
			return List.of(getScheduleCommand());
		}
		
		return super.compileInput(inputName, args, source, instigator);
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		Function fireFunction = new Function(getFireFunction());
		fireFunction.commands.addAll(compileOutput("on_fire"));
		fireFunction.commands.add(getScheduleCommand());
		datapack.functions.add(fireFunction);
		
		CompoundTag storage = new CompoundTag();
		storage.putBoolean("enabled", startActive());
		datapack.defaultStorage.put(LogicUtils.getEntityStorage(this), storage);
		
		if (startActive()) {
			if (fireOnActivate()) {
				getLevel().initFunction().commands.add(new FunctionCommand(fireFunction));
			} else {
				getLevel().initFunction().commands.add(getScheduleCommand());
			}
		}
		
		return super.compileLogic(datapack);
	}
	
	public Identifier getFireFunction() {
		return LogicUtils.getEntityFunction(this, "fire");
	}
	
	public Conditional isEnabled() {
		return new ExecuteCommand.DataStorageConditional(LogicUtils.getEntityStorage(this), "{enabled:1b}");
	}
	
	public Command getScheduleCommand() {
		return new ExecuteCommandBuilder().executeIf(isEnabled()).run(new ScheduleCommand(getFireFunction(), getTime(), Mode.REPLACE));
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/clock.png";
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/logic_timer.sdoc", super.getDocumentation());
	}
	
}
