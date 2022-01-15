package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Collection;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.StringAttribute;
import org.scaffoldeditor.scaffold.level.io.OutputDeclaration;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.datapack.Function;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

/**
 * Creates an entrypoint allowing external functions to trigger IO events.
 * @author Igrium
 */
public class LogicEntryPoint extends LogicEntity {
	
	public static void register() {
		EntityRegistry.registry.put("logic_entrypoint", LogicEntryPoint::new);
	}

	public LogicEntryPoint(Level level, String name) {
		super(level, name);
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/entrypoint.png";
	}

	@Attrib
	private StringAttribute identifier = new StringAttribute(getLevel().getName()+":"+getName());

	
	@Override
	public Collection<OutputDeclaration> getDeclaredOutputs() {
		Collection<OutputDeclaration> out = super.getDeclaredOutputs();
		out.add(() -> "on_trigger");
		return out;
	}
	
	/**
	 * Get the function that will be created.
	 */
	public Identifier getIdentifier() {
		return new Identifier(identifier.getValue());
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		Function function = new Function(getIdentifier());
		function.commands.addAll(compileOutput("on_trigger"));
		datapack.functions.add(function);
		
		return super.compileLogic(datapack);
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/logic_entrypoint.sdoc", super.getDocumentation());
	}
}
