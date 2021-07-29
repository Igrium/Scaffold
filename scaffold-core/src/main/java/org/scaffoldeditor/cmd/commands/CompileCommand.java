package org.scaffoldeditor.cmd.commands;

import org.scaffoldeditor.cmd.ScaffoldCommandSource;
import org.scaffoldeditor.cmd.arguments.GamemodeArgumentType;
import org.scaffoldeditor.cmd.arguments.PathArgumentType;
import org.scaffoldeditor.scaffold.level.LevelData.GameType;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EnumAttribute;
import org.scaffoldeditor.scaffold.compile.Compiler;
import org.scaffoldeditor.scaffold.compile.Compiler.*;


import static org.scaffoldeditor.cmd.CommandUtil.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class CompileCommand {
	public static void register(CommandDispatcher<ScaffoldCommandSource> dispatcher) {
		LiteralCommandNode<ScaffoldCommandSource> compile = literal("compile")
				.then(argument("targetPath", PathArgumentType.path())
						.executes(command -> {
							return compile(command.getSource(), PathArgumentType.getPath(command, "targetPath"),
									GameType.ADVENTURE, false, true);
						})
						.then(argument("gamemode", GamemodeArgumentType.gamemode())
								.executes(command -> {
									GameType type = GamemodeArgumentType.getGameType(command, "gamemode");
									return compile(command.getSource(),
										PathArgumentType.getPath(command, "targetPath"), type,
											(type == GameType.CREATIVE || type == GameType.SPECTATOR), true);
								})
								.then(argument("cheats", BoolArgumentType.bool())
										.executes(command -> {
											return compile(command.getSource(),
													PathArgumentType.getPath(command, "targetPath"),
													GamemodeArgumentType.getGameType(command, "gamemode"),
													BoolArgumentType.getBool(command, "cheats"), true);
										})
										.then(argument("full", BoolArgumentType.bool())
												.executes(command -> {
													return compile(command.getSource(),
															PathArgumentType.getPath(command, "targetPath"),
															GamemodeArgumentType.getGameType(command, "gamemode"),
															BoolArgumentType.getBool(command, "cheats"),
															BoolArgumentType.getBool(command, "full"));
												}))))).build();
		
		dispatcher.getRoot().addChild(compile);
	}
	
	private static int compile(ScaffoldCommandSource source, Path target, GameType gameType, boolean cheats, boolean full) {
		if (source.getContext().getLevel() == null) {
			source.printError("No level loaded!");
			return 0;
		}
		
		Map<String, Attribute<?>> args = new HashMap<>();
		args.put("gameType", new EnumAttribute<>(gameType));
		args.put("cheats", new BooleanAttribute(cheats));
		args.put("full", new BooleanAttribute(full));
		
		source.getOut().println("Compiling to '"+target+"' with settings: "+args);
		
		Compiler compiler = source.getContext().getProject().getCompiler();
		CompileResult result = compiler.compile(source.getContext().getLevel(), target, args, new CompileProgressListener() {
			
			@Override
			public void println(String string) {
				source.getOut().println(string);
			}
			
			@Override
			public void onError(String description) {
				source.printError(description);
			}
			
			@Override
			public void onCompileProgress(float percent, String description) {
				source.getOut().println(description);
			}
		});
		
		if (result.endStatus == CompileEndStatus.FINISHED) {
			source.getOut().println("Compile complete!");
		} else if (result.endStatus == CompileEndStatus.FAILED) {
			source.printError("Compile failed! "+result.errorMessage);
			return 0;
		}
		
		return 1;
	}
}
