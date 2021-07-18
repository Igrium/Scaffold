package org.scaffoldeditor.cmd;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

public final class CommandUtil {
	private CommandUtil() {}
	
	public static LiteralArgumentBuilder<ScaffoldCommandSource> literal(String literal) {
		return LiteralArgumentBuilder.literal(literal);
	}
	
	public static <T> RequiredArgumentBuilder<ScaffoldCommandSource, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
	
}
