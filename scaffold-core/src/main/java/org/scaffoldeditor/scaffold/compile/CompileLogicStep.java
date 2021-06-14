package org.scaffoldeditor.scaffold.compile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.scaffoldeditor.scaffold.compile.Compiler.CompileProgressListener;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.datapack.Command;
import org.scaffoldeditor.scaffold.logic.datapack.Function;

public class CompileLogicStep implements CompileStep {

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args, CompileProgressListener listener) {
		Path datapackFolder = target.resolve("datapacks");
		
		// Create tick and init functions
		Function initFunction = new Function(level.getName(), "init");
		Function tickFunction = new Function(level.getName(), "tick");		
		level.setInitFunction(initFunction);
		level.setTickFunction(tickFunction);
		
		// Create datapack
		Datapack datapack = new Datapack(level.getProject(), level.getName());
		datapack.functions.add(initFunction);
		datapack.functions.add(tickFunction);
		
		datapack.loadFunctions.add(initFunction.getMeta());
		datapack.tickFunctions.add(tickFunction.getMeta());
		
		// Respawn scoreboard entity.
		initFunction.commands.add(Command.fromString("kill "+level.getScoreboardEntity().getTargetSelector().compile()));
		initFunction.commands.add(level.summonScoreboardEntity());

		// Compile entities
		for (Entity ent : level.getEntities().values()) {
			if (!ent.compileLogic(datapack) && listener != null) {
				listener.onError("Failed to compile logic for entity: "+ent.getName());
			};
		}
		
		// Compile datapack
		try {
			datapack.compile(datapackFolder.resolve(level.getProject().getName()));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Compiling logic...";
	}

	@Override
	public String getID() {
		return "compile_logic";
	}
}
