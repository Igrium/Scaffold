package org.scaffoldeditor.cmd.commands;

import org.scaffoldeditor.cmd.ScaffoldCommandSource;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.level.stack.StackItem;
import org.scaffoldeditor.scaffold.level.stack.StackItem.ItemType;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.io.PrintStream;

import static org.scaffoldeditor.cmd.CommandUtil.*;

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
