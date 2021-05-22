package org.scaffoldeditor.scaffold.compile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.scaffoldeditor.scaffold.compile.Compiler.CompileProgressListener;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.MCFunction;

public class CompileLogicStep implements CompileStep {

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args, CompileProgressListener listener) {
		Path datapackFolder = target.resolve("datapacks");
		
		// Create tick and init functions
		MCFunction initFunction = new MCFunction("init");
		MCFunction tickFunction = new MCFunction("tick");		
		level.setInitFunction(initFunction);
		level.setTickFunction(tickFunction);
		
		// Create datapack
		Datapack datapack = new Datapack(level.getProject(), level.getName());
		datapack.functions.add(initFunction);
		datapack.functions.add(tickFunction);
		
		datapack.loadFunctions.add(datapack.formatFunctionCall(initFunction));
		datapack.tickFunctions.add(datapack.formatFunctionCall(tickFunction));
		
		// Respawn scoreboard entity.
		initFunction.addCommand("kill " + level.getScoreboardEntity().getTargetSelector());
		initFunction.addCommand(level.summonScoreboardEntity());

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

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args) {
		return execute(level, target, args, null);
	}

}
