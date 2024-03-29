package org.scaffoldeditor.scaffold.compile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileProgressListener;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.LevelData.GameType;

public class SetupStep implements CompileStep {

	@Override
	public boolean execute(Level level, Path target, Map<String, Attribute<?>> args, CompileProgressListener listener) {
		LogManager.getLogger().info("Target path: "+target);
		if (target.toFile().exists()) {
			try {
				FileUtils.deleteDirectory(target.toFile());
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		
		boolean cheats = false;
		if (args.get("cheats") instanceof BooleanAttribute) {
			cheats = ((BooleanAttribute) args.get("cheats")).getValue();
		}
		
		GameType gameType = GameType.ADVENTURE;
		Object gameTypeAtt = args.get("gameType").getValue();
		if (gameTypeAtt instanceof GameType) {
			gameType = (GameType) gameTypeAtt;
		}
		
		// Make world folder
		target.toFile().mkdir();

		// Compile levelData
		try {
			level.levelData().compileFile(target.resolve("level.dat").toFile(), cheats, gameType);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	@Override
	public boolean isRequired() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Initializing...";
	}

	@Override
	public String getID() {
		return "setup";
	}

}
