package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Collection;
import java.util.Map;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EnumAttribute;
import org.scaffoldeditor.scaffold.level.entity.logic.TriggerMultiple.ActivatorScope;
import org.scaffoldeditor.scaffold.level.entity.util.ToolBrushEntity;
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

import net.querz.nbt.tag.ByteTag;

public class TriggerIndividual extends ToolBrushEntity {
	
	public static void register() {
		EntityRegistry.registry.put("trigger_individual", TriggerIndividual::new);
	}

	public TriggerIndividual(Level level, String name) {
		super(level, name);
	}
	
	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = super.getDefaultAttributes();
		map.put("scope", new EnumAttribute<>(ActivatorScope.PLAYERS));
		map.put("start_disabled", new BooleanAttribute(false));
		map.put("use_event_functions", new BooleanAttribute(false));
		return map;
	}
	
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
		return (ActivatorScope) getAttribute("scope").getValue();
	}
	
	/**
	 * Get a target selector that will select all entities touching the trigger.
	 */
	public TargetSelector getSelector() {
		String prefix = (getScope() == ActivatorScope.PLAYERS) ? "@a" : "@e";
		Vector3f pos = getPosition();
		Vector3f dPos = getEndPoint();
		
		return TargetSelector.fromString(prefix+"[x="+pos.x+",y="+pos.y+",z="+pos.z+",dx="+dPos.x+",dy="+dPos.y+",dz="+dPos.z+"]");
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
		return (Boolean) getAttribute("use_event_functions").getValue();
	}

	@Override
	public String getTexture() {
		return "scaffold:textures/editor/trigger.png";
	}

}
