package org.scaffoldeditor.scaffold.compile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileProgressListener;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.AbstractPack.OutputMode;
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
		
		datapack.loadFunctions.add(initFunction.getID());
		datapack.tickFunctions.add(tickFunction.getID());

		// Compile entities
		for (Entity ent : level.getLevelStack()) {
			if (!ent.compileLogic(datapack) && listener != null) {
				listener.onError("Failed to compile logic for entity: "+ent.getName());
			};
		}
		
		// Compile datapack
		try {
			datapack.compile(datapackFolder.resolve(level.getProject().getName()).toFile(), OutputMode.FOLDER);
			datapack.writeStorage(target.resolve("data"));
		} catch (IOException e) {
			LogManager.getLogger().error(e);
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
