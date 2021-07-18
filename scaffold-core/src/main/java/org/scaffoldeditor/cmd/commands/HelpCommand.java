package org.scaffoldeditor.cmd.commands;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import static org.scaffoldeditor.cmd.CommandUtil.*;

import java.util.Map;

import org.scaffoldeditor.cmd.ScaffoldCommandSource;

public class HelpCommand {
	public static void register(CommandDispatcher<ScaffoldCommandSource> dispatcher) {
		LiteralCommandNode<ScaffoldCommandSource> help = literal("help")
				.executes(command -> {
					Map<CommandNode<ScaffoldCommandSource>, String> map = dispatcher
							.getSmartUsage(dispatcher.getRoot(), command.getSource());
					
					for (String value : map.values()) {
						command.getSource().getOut().println(value);
					}
					
					return map.size();
				})
				.then(argument("command", StringArgumentType.greedyString())
						.executes(command -> {
							String name = StringArgumentType.getString(command, "command");
							ParseResults<ScaffoldCommandSource> results = dispatcher
									.parse(name, command.getSource());
							
							if (results.getContext().getNodes().isEmpty()) {
								throw new SimpleCommandExceptionType(() -> "Unknown command: "+name).create();
							}
							
							Map<CommandNode<ScaffoldCommandSource>, String> map = dispatcher.getSmartUsage(
									Iterables.getLast(results.getContext().getNodes()).getNode(), command.getSource());
							
							for (String value : map.values()) {
								command.getSource().getOut().println(value);
							}
							
							return map.size();
						}))
				.build();
		
		dispatcher.getRoot().addChild(help);
		
	}
}
