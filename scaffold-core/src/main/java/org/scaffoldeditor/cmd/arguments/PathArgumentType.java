package org.scaffoldeditor.cmd.arguments;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class PathArgumentType implements ArgumentType<Path> {

	@Override
    public Path parse(StringReader reader) throws CommandSyntaxException {
        String str = reader.readString().strip();
		if (str.charAt(0) == '\'' || str.charAt(0) == '"') {
			str = str.substring(1, str.length() - 1);
		}

		try {
			return Paths.get(str);
		} catch (InvalidPathException e) {
			throw new SimpleCommandExceptionType(() -> e.getLocalizedMessage()).createWithContext(reader);
		}
    }

	public static PathArgumentType path() {
		return new PathArgumentType();
	}

	public static Path getPath(CommandContext<?> context, String name) {
		return context.getArgument(name, Path.class);
	}

}
