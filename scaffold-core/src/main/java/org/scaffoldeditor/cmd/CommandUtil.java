package org.scaffoldeditor.cmd;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public final class CommandUtil {
	private CommandUtil() {}
	
	public static LiteralArgumentBuilder<ScaffoldCommandSource> literal(String literal) {
		return LiteralArgumentBuilder.literal(literal);
	}
	
	public static <T> RequiredArgumentBuilder<ScaffoldCommandSource, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
	
	public static String readStructuredString(StringReader reader) throws CommandSyntaxException {
		StringBuilder builder = new StringBuilder();

		int bracketIndex = 0;
		int curlyIndex = 0;
		int parenthesisIndex = 0;
		boolean quote = false;
		boolean singleQuote = false;

		while (true) {
			if (!reader.canRead()) {
				throw new SimpleCommandExceptionType(() -> "Unbalenced brackets.").createWithContext(reader);
			}

			char c = reader.read();
			if (c == '\\') {
				reader.read();
				continue;
			}

			if (quote) {
				if (c == '"') quote = false;
				continue;
			} else if (singleQuote) {
				if (c == '\'') singleQuote = false;
				continue;
			}

			if (c == '[') bracketIndex++;
			else if (c == ']') bracketIndex--;
			else if (c == '{') curlyIndex++;
			else if (c == '}') curlyIndex--;
			else if (c == '(') parenthesisIndex++;
			else if (c == ')') parenthesisIndex --;

			if (bracketIndex < 0 || curlyIndex < 0 || parenthesisIndex < 0) {
				throw new SimpleCommandExceptionType(
						() -> "Closing bracket does not have a corresponding opening bracket.")
								.createWithContext(reader);
			}

			if (bracketIndex == 0 && curlyIndex == 0 && parenthesisIndex == 0) {
				break;
			}
		}
		
		return builder.toString();
	}
}
