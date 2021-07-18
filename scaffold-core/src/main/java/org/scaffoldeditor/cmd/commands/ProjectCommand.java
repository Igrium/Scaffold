package org.scaffoldeditor.cmd.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import static org.scaffoldeditor.cmd.CommandUtil.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.scaffoldeditor.cmd.CommandUtil;
import org.scaffoldeditor.cmd.ScaffoldCommandSource;
import org.scaffoldeditor.cmd.ScaffoldTerminalContext;
import org.scaffoldeditor.scaffold.core.Constants;
import org.scaffoldeditor.scaffold.core.Project;

import static com.mojang.brigadier.arguments.StringArgumentType.*;

public class ProjectCommand {
	public static void register(CommandDispatcher<ScaffoldCommandSource> dispatcher) {
		
		LiteralCommandNode<ScaffoldCommandSource> project = literal("project").build();
		build(project);
		
		dispatcher.getRoot().addChild(project);
	}
	
	private static void build(LiteralCommandNode<ScaffoldCommandSource> root) {

		LiteralCommandNode<ScaffoldCommandSource> open = literal("open")
				.then(CommandUtil.argument("path", StringArgumentType.greedyString())
				.executes(command -> {
					String path = getString(command, "path");
					command.getSource().getOut().println("Opening project...");
					
					try {
						command.getSource().getContext().loadProject(path);
					} catch (IOException e) {
						command.getSource().printError("Error opening project: " + e.getLocalizedMessage());
						return 0;
					}

					return 1;
				})).build();
		
		LiteralCommandNode<ScaffoldCommandSource> print = literal("print")
				.executes(command -> {
					ScaffoldCommandSource source = command.getSource();
					Project project = source.getContext().getProject();
					if (project == null) {
						source.getOut().println("There is no project currently loaded.");
					} else {
						source.getOut().println("Project folder: "+project.getProjectFolder());
					}
					
					return 1;
				}).build();
		
		LiteralCommandNode<ScaffoldCommandSource> close = literal("close")
				.executes(command -> {
					ScaffoldTerminalContext context = command.getSource().getContext();
					if (context.getProject() == null) {
						command.getSource().getOut().println("No project open.");
						return 0;
					}
					
					if (context.getLevel() != null) {
						command.getSource().getOut().print("Closing level...");
					}
					
					context.setProject(null);
					command.getSource().getOut().println("Project closed.");
					
					return 1;
				}).build();
		
		LiteralCommandNode<ScaffoldCommandSource> init = literal("init")
				.then(argument("title", StringArgumentType.string()).then(argument("path", StringArgumentType.greedyString())
				.executes(command -> {
					Path path = Paths.get(getString(command, "path"));
					if (path.resolve(Constants.GAMEINFONAME).toFile().exists()) {
						command.getSource().getOut().println("Project already exists at "+path);
						return 0;
					}
					
					command.getSource().getOut().println("Initializing project at "+path+"...");
					
					try {
						Project project = Project.init(path.toString(), getString(command, "title"));
						command.getSource().getContext().setProject(project);
					} catch (IOException e) {
						command.getSource().printError("Error initializing project: " + e.getLocalizedMessage());
						return 0;
					}
					
					return 1;
				}))).build();
		
		root.addChild(open);
		root.addChild(print);
		root.addChild(close);
		root.addChild(init);
	}
	

}
