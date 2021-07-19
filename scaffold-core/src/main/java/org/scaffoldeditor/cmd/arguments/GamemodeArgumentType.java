package org.scaffoldeditor.cmd.arguments;

import java.util.Arrays;
import java.util.Collection;

import org.scaffoldeditor.scaffold.level.LevelData.GameType;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class GamemodeArgumentType implements ArgumentType<GameType> {

	@Override
	public GameType parse(StringReader reader) throws CommandSyntaxException {
		String str = reader.readString();
		try {
			return GameType.valueOf(str.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new SimpleCommandExceptionType(() -> "Unknown gamemode type: "+str).createWithContext(reader);
		}
	}
	
	public static GameType getGameType(CommandContext<?> context, String name) {
		return context.getArgument(name, GameType.class);
	}
	
	public static GamemodeArgumentType gamemode() {
		return new GamemodeArgumentType();
	}
	
	@Override
	public Collection<String> getExamples() {
		return Arrays.asList(new String[] { "adventure", "survival", "creative", "spectator" });
	}
}
