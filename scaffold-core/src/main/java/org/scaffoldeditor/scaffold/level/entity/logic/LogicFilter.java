package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Collection;
import java.util.List;

import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.FilterAttribute;
import org.scaffoldeditor.scaffold.level.io.InputDeclaration;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

public class LogicFilter extends LogicEntity {
	
	public static void register() {
		EntityRegistry.registry.put("logic_filter", LogicFilter::new);
	}

	public LogicFilter(Level level, String name) {
		super(level, name);
	}

	@Attrib
	FilterAttribute filter = new FilterAttribute();
	
	@Override
	public Collection<OutputDeclaration> getDeclaredOutputs() {
		Collection<OutputDeclaration> out = super.getDeclaredOutputs();
		out.add(() -> "on_trigger");
		return out;
	}
	
	@Override
	public Collection<InputDeclaration> getDeclaredInputs() {
		Collection<InputDeclaration> in = super.getDeclaredInputs();
		in.add(() -> "trigger");
		return in;
	}
	
	@Override
	public List<Command> compileInput(String inputName, List<Attribute<?>> args, Entity source, Entity instigator) {
		if (inputName.equals("trigger")) {
			ExecuteCommandBuilder filter = getFilter();
			return compileOutput("on_trigger", instigator).stream().map(command -> (Command) filter.run(command)).toList();
		}
		
		return super.compileInput(inputName, args, source, instigator);
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/filter.png";
	}
	
	public ExecuteCommandBuilder getFilter() {
		return ((FilterAttribute) getAttribute("filter")).getValue();
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/logic_filter.sdoc", super.getDocumentation());
	}
}
