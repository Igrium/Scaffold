package org.scaffoldeditor.cmd.commands;

import org.scaffoldeditor.cmd.CommandUtil;
import org.scaffoldeditor.cmd.ScaffoldCommandSource;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class ExitCommand {
	public static void register(CommandDispatcher<ScaffoldCommandSource> dispatcher) {
		LiteralCommandNode<ScaffoldCommandSource> exit = CommandUtil.literal("exit").executes(command -> {
			command.getSource().exit();
			return 1;
		}).build();
		dispatcher.getRoot().addChild(exit);
	}
}
