package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EnumAttribute;
import org.scaffoldeditor.scaffold.level.entity.util.ToolBrushEntity;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.LogicUtils;
import org.scaffoldeditor.scaffold.logic.datapack.Function;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.DataCommand;
import org.scaffoldeditor.scaffold.logic.datapack.commands.DataCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.*;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;

import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.commands.FunctionCommand;

public class TriggerMultiple extends ToolBrushEntity {
	
	public enum ActivatorScope { PLAYERS, ENTITIES }
	
	public static void register() {
		EntityRegistry.registry.put("trigger_multiple", TriggerMultiple::new);
		EnumAttribute.enumRegistry.put("trigger.activator_scope", ActivatorScope.class);
	}

	public TriggerMultiple(Level level, String name) {
		super(level, name);
	}
	
	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = super.getDefaultAttributes();
		map.put("scope", new EnumAttribute<ActivatorScope>(ActivatorScope.PLAYERS));
		map.put("start_disabled", new BooleanAttribute(false));
		map.put("trigger_once", new BooleanAttribute(false));
		return map;
	}
	
	public ActivatorScope getScope() {
		return (ActivatorScope) ((EnumAttribute<?>) getAttribute("scope")).getValue();
	}
	
	public boolean startDisabled() {
		return ((BooleanAttribute) getAttribute("start_disabled")).getValue();
	}
	
	public boolean triggerOnce() {
		return ((BooleanAttribute) getAttribute("trigger_once")).getValue();
	}
	
	@Override
	public Collection<OutputDeclaration> getDeclaredOutputs() {
		Collection<OutputDeclaration> out = super.getDeclaredOutputs();
		out.add(new OutputDeclaration() {
			
			@Override
			public String getName() {
				return "on_start_touch";
			}
		});
		
		out.add(new OutputDeclaration() {
			
			@Override
			public String getName() {
				return "on_end_touch";
			}
		});
		
		out.add(new OutputDeclaration() {
			
			@Override
			public String getName() {
				return "on_tick";
			}
		});

		return out;
	}
	
	@Override
	public Collection<InputDeclaration> getDeclaredInputs() {
		Collection<InputDeclaration> inputs = super.getDeclaredInputs();
		inputs.add(() -> "enable");
		inputs.add(() -> "disable");
		return inputs;
	}
	
	@Override
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source, Entity instigator) {
		if (inputName.equals("enable")) {
			return List.of(setDisabled(false));
		}
		if (inputName.equals("disable")) {
			return List.of(setDisabled(true));
		}
		
		return super.compileInput(inputName, args, source, instigator);
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		Function handleStartTouch = new Function(LogicUtils.getEntityFunction(this, "handle_start_touch"));
		handleStartTouch.commands.addAll(compileOutput("on_start_touch"));
		handleStartTouch.commands.add(new DataCommandBuilder().modify().storage(LogicUtils.getEntityStorage(this))
				.set().path("isTouching").value(new ByteTag(true)).build());	
		datapack.functions.add(handleStartTouch);
		
		Function handleEndTouch = new Function(LogicUtils.getEntityFunction(this, "handle_end_touch"));
		handleEndTouch.commands.addAll(compileOutput("on_end_touch"));
		handleEndTouch.commands.add(new DataCommandBuilder().modify().storage(LogicUtils.getEntityStorage(this))
				.set().path("isTouching").value(new ByteTag(false)).build());
		if (triggerOnce()) handleEndTouch.commands.add(setDisabled(true));
		datapack.functions.add(handleEndTouch);
		
		Function tickFunction = new Function(LogicUtils.getEntityFunction(this, "tick"));
		tickFunction.commands.add(new ExecuteCommandBuilder().executeUnless(isTouching()).executeUnless(isDisabled())
				.executeIf(new EntityConditional(getSelector())).run(new FunctionCommand(handleStartTouch)));
		
		tickFunction.commands.add(new ExecuteCommandBuilder().executeIf(isTouching()).executeUnless(isDisabled())
				.executeUnless(new EntityConditional(getSelector())).run(new FunctionCommand(handleEndTouch)));
		
		tickFunction.addExecuteBlock(new ExecuteCommandBuilder().executeIf(isTouching()), compileOutput("on_tick"));
		
		datapack.functions.add(tickFunction);
		datapack.tickFunctions.add(tickFunction.getID());
		
		CompoundTag defaultStorage = new CompoundTag();
		defaultStorage.put("isDisabled", new ByteTag(startDisabled()));
		defaultStorage.put("isTouching", new ByteTag(false));
		datapack.defaultStorage.put(LogicUtils.getEntityStorage(this), defaultStorage);
		
		return super.compileLogic(datapack);
	}
	
	/**
	 * Get a target selector that will select all entities touching the trigger.
	 */
	public TargetSelector getSelector() {
		String prefix = (getScope() == ActivatorScope.PLAYERS) ? "@a" : "@e";
		Vector3f pos = getPosition();
		Vector3f dPos = getEndPoint();
		
		return TargetSelector.fromString(prefix + "[x=" + pos.x + ",y=" + pos.y + ",z=" + pos.z + ",dx=" + (dPos.x - 1)
				+ ",dy=" + (dPos.y - 1) + ",dz=" + (dPos.z - 1) + "]");
	}
	
	/**
	 * Obtain a conditional that tests whether there's an entity touching this trigger.
	 */
	public Conditional isTouching() {
		return new DataStorageConditional(LogicUtils.getEntityStorage(this), "{isTouching:1b}");
	}
	
	/**
	 * Obtain a conditional that tests whether this trigger is disabled.
	 */
	public Conditional isDisabled() {
		return new DataStorageConditional(LogicUtils.getEntityStorage(this), "{isDisabled:1b}");
	}
	
	/**
	 * Obtain a command that will enable or disable this trigger. <b>Does not affect
	 * {@link #isTouching()}!</b>
	 * 
	 * @param disabled Whether to enable or disable.
	 * @return The generated command.
	 */
	public DataCommand setDisabled(boolean disabled) {
		return new DataCommandBuilder().modify().storage(LogicUtils.getEntityStorage(this)).set().path("isDisabled")
				.value(new ByteTag(disabled)).build();
	}

	@Override
	public String getTexture() {
		return "scaffold:textures/editor/trigger.png";
	}
	
	@Override
	public boolean isGridLocked() {
		return true;
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/trigger_multiple.sdoc");
	}
}
