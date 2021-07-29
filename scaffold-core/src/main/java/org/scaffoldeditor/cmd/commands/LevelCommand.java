package org.scaffoldeditor.cmd.commands;

import org.fusesource.jansi.Ansi.Color;
import org.scaffoldeditor.cmd.ScaffoldCommandSource;
import org.scaffoldeditor.cmd.ScaffoldTerminalContext;
import org.scaffoldeditor.cmd.arguments.PathArgumentType;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;

import static org.scaffoldeditor.cmd.CommandUtil.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class LevelCommand {
	public static void register(CommandDispatcher<ScaffoldCommandSource> dispatcher) {
		LiteralCommandNode<ScaffoldCommandSource> root = literal("level").build();
		
		LiteralCommandNode<ScaffoldCommandSource> open = literal("open")
				.then(argument("path", PathArgumentType.path())
						.executes(command -> {
							ScaffoldTerminalContext context = command.getSource().getContext();
							Project project = context.getProject();
							if (project == null) {
								command.getSource().printError("No project loaded!");
								return 0;
							}
							File levelFile = PathArgumentType.getPath(command, "path").toFile();
							
							try {
								context.openLevel(levelFile);
							} catch (IOException e) {
								command.getSource().printError("Error loading level: "+e.getLocalizedMessage());
								return 0;
							}
							
							if (!levelFile.toPath().startsWith(context.getProject().getProjectFolder())) {
								command.getSource().printColor(Color.YELLOW, "Warning: level is not within project folder and may be unstable!");
							}
							
							return 1;
						})).build();
		
		LiteralCommandNode<ScaffoldCommandSource> close = literal("close")
				.executes(command -> {
					ScaffoldCommandSource source = command.getSource();
					if (source.getContext().getLevel() != null) {
						source.getContext().setLevel(null);
						command.getSource().getOut().println("Level closed.");
						return 1;
					} else {
						command.getSource().getOut().println("No level open!");
						return 0;
					}
					
				}).build();
		
		LiteralCommandNode<ScaffoldCommandSource> create = literal("create")
				.then(argument("name", PathArgumentType.path())
						.executes(command -> {
							ScaffoldTerminalContext context = command.getSource().getContext();
							Project project = context.getProject();
							if (project == null) {
								command.getSource().printError("No project loaded!");
								return 0;
							}
							
							Path levelPath = PathArgumentType.getPath(command, "path");
							if (!levelPath.getFileName().endsWith(".mclevel")) {
								levelPath = levelPath.getParent().resolve(levelPath);
							}
							if (!levelPath.isAbsolute()) {
								levelPath = context.getProject().getProjectFolder().resolve(levelPath);
							}
							
							File levelFile = levelPath.toFile();
							if (levelFile.exists()) {
								command.getSource().printError("File already exists!");
								return 0;
							}
							
							context.setLevel(new Level(project));
							try {
								context.save(levelFile);
							} catch (IOException e) {
								command.getSource().printError("Error creating level: "+e.getLocalizedMessage());
								context.setLevel(null);
							}
							
							if (!levelPath.startsWith(context.getProject().getProjectFolder())) {
								command.getSource().printColor(Color.YELLOW, "Warning: level is not within project folder and may be unstable!");
							}
							
							return 1;
						})).build();
		
		LiteralCommandNode<ScaffoldCommandSource> get = literal("get")
				.executes(command -> {
					Level level = command.getSource().getContext().getLevel();
					if (level == null) {
						command.getSource().printError("No level loaded!");
						return 0;
					}
					
					command.getSource().getOut()
							.println("Name: '" + level.getName() + "', Pretty Name: '" + level.getPrettyName()
									+ ", File: '" + command.getSource().getContext().getLevelFile() + "'");
					
					return 1;
				})
				.build();
		
		LiteralCommandNode<ScaffoldCommandSource> save = literal("save")
				.executes(command -> {
					return save(command.getSource(), null);
				}).then(argument("path", StringArgumentType.greedyString())
						.executes(command -> {
							return save(command.getSource(), StringArgumentType.getString(command, "path"));
						}))
				.build();

		root.addChild(open);
		root.addChild(create);
		root.addChild(close);
		root.addChild(get);
		root.addChild(save);
		
		dispatcher.getRoot().addChild(root);
	}
	
	private static int save(ScaffoldCommandSource source, String path) {
		Level level = source.getContext().getLevel();
		if (level == null) {
			source.printError("No level loaded!");
			return 0;
		}
		
		if (path == null) {
			path = source.getContext().getLevelFile().getAbsolutePath();
			if (path == null) {
				source.printError("Tried to save a level implicitly but there is no cached level file.");
				return 0;
			}
		}
		
		Path levelPath = Paths.get(path);
		if (!levelPath.isAbsolute()) {
			levelPath = source.getContext().getProject().getProjectFolder().resolve(levelPath);
		}
		
		try {
			source.getContext().save(levelPath.toFile());
		} catch (IOException e) {
			source.printError("Error saving level: "+e.getLocalizedMessage());
			return 0;
		}
		
		source.getOut().println("Level saved as '" + levelPath + "'");
		
		return 1;
	}
}
