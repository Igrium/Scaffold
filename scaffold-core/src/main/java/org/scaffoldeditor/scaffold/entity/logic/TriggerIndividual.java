package org.scaffoldeditor.scaffold.entity.logic;

import java.util.Collection;

import org.joml.Vector3dc;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.EnumAttribute;
import org.scaffoldeditor.scaffold.entity.logic.TriggerMultiple.ActivatorScope;
import org.scaffoldeditor.scaffold.entity.util.ToolBrushEntity;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.LogicUtils;
import org.scaffoldeditor.scaffold.logic.datapack.Function;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.commands.DataCommand;
import org.scaffoldeditor.scaffold.logic.datapack.commands.DataCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.Conditional;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.DataStorageConditional;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

import net.querz.nbt.tag.ByteTag;

public class TriggerIndividual extends ToolBrushEntity {
	
	public static void register() {
		EntityRegistry.registry.put("trigger_individual", TriggerIndividual::new);
	}

	public TriggerIndividual(Level level, String name) {
		super(level, name);
	}

	@Attrib
	EnumAttribute<ActivatorScope> scope = new EnumAttribute<TriggerMultiple.ActivatorScope>(ActivatorScope.PLAYERS);

	@Attrib(name = "start_disabled")
	BooleanAttribute startDisabled = new BooleanAttribute(false);

	@Attrib(name = "use_event_functions")
	BooleanAttribute useEventFunctions = new BooleanAttribute(true);
	
	@Override
	public Collection<OutputDeclaration> getDeclaredOutputs() {
		Collection<OutputDeclaration> out = super.getDeclaredOutputs();
		
		out.add(new OutputDeclaration() {
			public String getName() {
				return "on_entity_tick";
			}
		});
		
		out.add(new OutputDeclaration() {
			public String getName() {
				return "on_start_touch";
			}
		});
		
		out.add(new OutputDeclaration() {
			public String getName() {
				return "on_end_touch";
			}
		});
		
		return out;
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		Function tickFunction = new Function(LogicUtils.getEntityFunction(this, "tick"));
		tickFunction.addExecuteBlock(new ExecuteCommandBuilder().executeUnless(isDisabled()).as(getSelector()),
				compileOutput("on_entity_tick"));
		datapack.functions.add(tickFunction);
		datapack.tickFunctions.add(tickFunction.getID());
		
//		if (useEventFunctions()) {
//			// Called in the scope of the touching entity.
//			Function handleStartTouch = new Function(LogicUtils.getEntityFunction(this, "handle_start_touch"));
//			handleStartTouch.commands.addAll(compileOutput("on_start_touch"));
//			
//		}
		
		return super.compileLogic(datapack);
	}
	
	
	public ActivatorScope getScope() {
		return scope.getValue();
	}
	
	/**
	 * Get a target selector that will select all entities touching the trigger.
	 */
	public TargetSelector getSelector() {
		String prefix = (getScope() == ActivatorScope.PLAYERS) ? "@a" : "@e";
		Vector3dc pos = getPosition();
		Vector3dc dPos = getEndPoint();
		
		return TargetSelector.fromString(prefix + "[x=" + pos.x() + ",y=" + pos.y() + ",z=" + pos.z() + ",dx=" + (dPos.x() - 1)
				+ ",dy=" + (dPos.y() - 1) + ",dz=" + (dPos.z() - 1) + "]");
	}
	
	/**
	 * Obtain a conditional that tests whether this trigger is disabled.
	 */
	public Conditional isDisabled() {
		return new DataStorageConditional(LogicUtils.getEntityStorage(this), "{isDisabled:1b}");
	}
	
	/**
	 * Obtain a command that will enable or disable this trigger.
	 * 
	 * @param disabled Whether to enable or disable.
	 * @return The generated command.
	 */
	public DataCommand setDisabled(boolean disabled) {
		return new DataCommandBuilder().modify().storage(LogicUtils.getEntityStorage(this)).set().path("isDisabled")
				.value(new ByteTag(disabled)).build();
	}
	
	public boolean useEventFunctions() {
		return useEventFunctions.getValue();
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
		return SDoc.loadAsset(getAssetManager(), "doc/trigger_individual.sdoc", super.getDocumentation());
	}
}
