package org.scaffoldeditor.cmd.commands;

import java.util.Collections;

import org.scaffoldeditor.cmd.ScaffoldCommandSource;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.level.stack.StackItem;
import org.scaffoldeditor.scaffold.level.stack.StackItem.ItemType;
import org.scaffoldeditor.scaffold.operation.AddEntityOperation;
import org.scaffoldeditor.scaffold.operation.DeleteEntityOperation;

import static org.scaffoldeditor.cmd.CommandUtil.*;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.io.PrintStream;

public class EntityCommand {
	public static void register(CommandDispatcher<ScaffoldCommandSource> dispatcher) {
		LiteralCommandNode<ScaffoldCommandSource> root = literal("entity").build();
		
		LiteralCommandNode<ScaffoldCommandSource> list = literal("list")
				.executes(command -> {
					Level level = command.getSource().getContext().getLevel();
					if (level == null) {
						command.getSource().printError("No level open!");
						return 0;
					}
					
					return printGroup(level.getLevelStack(), 0, command.getSource().getOut());
				})
				.build();
		
		root.addChild(list);

		LiteralCommandNode<ScaffoldCommandSource> create = literal("create")
				.then(argument("class", StringArgumentType.string())
						.then(argument("name", StringArgumentType.string()).executes(command -> {
							String registryName = StringArgumentType.getString(command, "class");
							Level level = command.getSource().getContext().getLevel();
							if (level == null) {
								command.getSource().printError("No level loaded!");
								return 0;
							}

							if (!EntityRegistry.registry.containsKey(registryName)) {
								command.getSource().printError("Unknown entity class: " + registryName);
								return 0;
							}
							String name = StringArgumentType.getString(command, "name");
							level.getOperationManager()
									.execute(new AddEntityOperation(level, registryName, name, new Vector3f(0, 0, 0)));
							command.getSource().getOut().println("Created new "+registryName);
							return 1;
						})))
				.build();

		root.addChild(create);

		LiteralCommandNode<ScaffoldCommandSource> remove = literal("remove")
				.then(argument("name", StringArgumentType.string()).executes((command) -> {
					Level level = command.getSource().getContext().getLevel();
					if (level == null) {
						command.getSource().printError("No level loaded!");
						return 0;
					}
					String name = StringArgumentType.getString(command, "name");
					Entity ent = level.getEntity(name);
					if (ent == null) {
						command.getSource().printError("No entity exists called '" + name + "'.");
						return 0;
					}

					level.getOperationManager().execute(new DeleteEntityOperation(level, Collections.singleton(ent)));

					return 0;
				})).build();

		root.addChild(remove);
		dispatcher.getRoot().addChild(root);
	}
	
	private static int printGroup(StackGroup group, int indent, PrintStream out) {
		out.println(" ".repeat(indent * 4) + "group: "+group.getName());
		indent++;
		int count = 0;
		for (StackItem item : group.items) {
			if (item.getType() == ItemType.ENTITY) {
				Entity ent = item.getEntity();
				out.println(" ".repeat(indent * 4) + ent.getName() + " ("+ent.registryName+")");
				count++;
			} else {
				count += printGroup(item.getGroup(), indent, out);
			}
		}
		return count;
	}
}
